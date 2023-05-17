package com.kmetabus.mypet;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.CoordinateTransformFactory;
import org.osgeo.proj4j.ProjCoordinate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class AnimalHospitalList {
    //myLatitude 현재 기기 좌표
    public static List<AnimalHospital>  getList( double myLatitude, double myLongitude,Context ctx, String gbn ) {

        List<AnimalHospital> hospitalList = new ArrayList<>();
        try {
        	Document document = null;
            String fileUrl = "http://kmetabus.com/cdh/data/pet_hospital.xml";
            String filePath = "pet_hospital.xml";
            try {
                if("NEW".equals(gbn) ){ // 서버에서 새로 data를 받는다.
//System.out.println("파일 존재 NEW"  );
                    downloadFile(ctx, fileUrl, filePath);
                }else{// app 내부저장소 파일에서 가져온다.
 //System.out.println("파일 존재 내부저장소"  );
                    FileInputStream fis = ctx.openFileInput(filePath);
                }
                // 파일 읽기와 관련된 코드
            } catch (FileNotFoundException e) {
//System.out.println("파일 존재 1"  );
                // 파일이 없을 때의 처리 코드
                downloadFile(ctx, fileUrl, filePath);
            }

            //String filePath = "D:\\work\\pet_hospital.xml";
            String xml = "";
            // string.getBytest()
            FileInputStream fis = ctx.openFileInput(filePath)  ;
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);

            xml = new String(buffer, StandardCharsets.UTF_8);
//System.out.println("xml"+xml);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            //Document document = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            //Document document = builder.parse(filePath);
            InputSource inputSource = new InputSource(new StringReader(xml));
            document = builder.parse(inputSource);
            
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("row");

            //���� (Latitude): 37.566295
            //�浵 (Longitude): 126.977945
            //double myLatitude = 37.566295; // 현위치
            //double myLongitude = 126.977945; // ���� ��ġ�� �浵
            Date date = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String isNew = null;
                    String today = null;
                    String name = element.getElementsByTagName("bplcNm").item(0).getTextContent();
                    String phone = element.getElementsByTagName("siteTel").item(0).getTextContent();
                    String address = element.getElementsByTagName("siteWhlAddr").item(0).getTextContent(); 
                    String sx = element.getElementsByTagName("x").item(0).getTextContent().trim();
                    String sy = element.getElementsByTagName("y").item(0).getTextContent().trim();
                    if( element.getElementsByTagName("isNew").getLength() >0 )
                        isNew = element.getElementsByTagName("isNew").item(0).getTextContent().trim();
                    if( element.getElementsByTagName("updateDt").getLength() >0 ) {
                        today = element.getElementsByTagName("updateDt").item(0).getTextContent().trim();
                        date = dateFormat.parse(today);
                    }

                    if("".equals(sx) ) {
                    	continue;
                    }
                    double x = Double.parseDouble(sx);
                    double y = Double.parseDouble(sy);
                    boolean isnew = Boolean.parseBoolean(isNew);
                    // UTM ��ǥ�� WGS84 ��ǥ��� ��ȯ�ϴ� �ڵ尡 �ʿ��մϴ�.
                    AnimalHospital hospital = null;
                    if(!isnew) {
                        ProjCoordinate wgs84Coordinate = convertUTMToWGS84(x, y);
                        //double[] latLng = getLatLngFromAddress(address);
                        hospital = new AnimalHospital(name, phone, address, wgs84Coordinate.y, wgs84Coordinate.x, isnew, date);
                    }else{// 신규건
//System.out.println("cdhgold 9"+x+" " +y );
                        hospital = new AnimalHospital(name, phone, address, x, y, isnew, date,myLatitude, myLongitude);
                    }
                    hospitalList.add(hospital);
                }
            }// end for
            // 현재좌표로 정렬
            //hospitalList.sort(Comparator.comparingDouble(h -> h.distanceTo(myLatitude, myLongitude)));
			// ���� ��ġ (WGS84 ��ǥ��)�� �Է��ϼ���.
			//double currentLatitude = 37.5665; // ��: �����û ����
			//double currentLongitude = 126.9780; // ��: �����û �浵

            // isNew는 30일간만 유효
            AtomicReference<Date> atodt = new AtomicReference<>();
            AtomicReference<Date> btodt = new AtomicReference<>();
            LocalDate nowdt = LocalDate.now();
			Collections.sort(hospitalList, (a, b) -> {
                Date aDate = a.getToday();
                Date bDate = b.getToday();

                atodt.set(a.getToday());
                btodt.set(b.getToday());
                Instant aInstant = atodt.get().toInstant();//UTC 기준으로 1970년 1월 1일 0시 0분 0초를 숫자 0
                ZoneId defaultZoneId = ZoneId.systemDefault();//서버 또는 클라이언트의 기본 시간대 설정에 따라 시간대를 결정
                LocalDate aLocalDate = aInstant.atZone(defaultZoneId).toLocalDate();
                long aDaysDifference = Math.abs(ChronoUnit.DAYS.between(nowdt, aLocalDate));
                Instant bInstant = b.getToday().toInstant();
                LocalDate bLocalDate = bInstant.atZone(defaultZoneId).toLocalDate();
                long bDaysDifference = Math.abs(ChronoUnit.DAYS.between(nowdt, bLocalDate));
 //System.out.println("cdhgold 1  nowdt "+nowdt+"    "+aLocalDate+"      "+aDaysDifference+"  "+ bLocalDate+ "    "+ bDaysDifference );
                if ((a.getIsNew()   && aDaysDifference <= 30) || ( b.getIsNew() && bDaysDifference <= 30)) {
                    //System.out.println("cdhgold 1");
                    return -1;

                } else {
                    //System.out.println("cdhgold 3");
                    double distanceA = a.distanceTo(myLatitude, myLongitude);
                    double distanceB = b.distanceTo(myLatitude, myLongitude);
                    return Double.compare(distanceA, distanceB);
                }

			});

	        /*
            for (AnimalHospital hospital : hospitalList) {
            	String addr = hospital.getAddress();
            	Double lat = hospital.getLatitude();
            	Double longi = hospital.getLongitude();
                System.out.println("���� �̸�: " + hospital.getName());
				System.out.println("��ȭ��ȣ: " + hospital.getPhone());
				System.out.println("�ּ�: " + addr);
				System.out.println("����: " + lat);
				System.out.println("�浵: " + longi);
				System.out.println("�Ÿ�: " + hospital.distanceTo(currentLatitude, currentLongitude) + " km");
				System.out.println();
            }
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
        ListViewModel.setDataList(hospitalList);
        return hospitalList;
    }
	public static ProjCoordinate convertUTMToWGS84(double x, double y ) {
		String koreanCRSCode = "EPSG:5174"; // KOREA 2000 / Central Belt
        String wgs84CRSCode = "EPSG:4326"; // WGS84

        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem koreanCRS = crsFactory.createFromName(koreanCRSCode);
        CoordinateReferenceSystem wgs84CRS = crsFactory.createFromName(wgs84CRSCode);

        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform transform = ctFactory.createTransform(koreanCRS, wgs84CRS);
        ProjCoordinate koreanCoordinate = new ProjCoordinate(x, y);
        ProjCoordinate wgs84Coordinate = new ProjCoordinate();
        transform.transform(koreanCoordinate, wgs84Coordinate);

        return wgs84Coordinate;
    }
    // 내부저장소에서 파일을 읽어온다.
    public  static String readFromFile(Context context, String fileName) {
        FileInputStream inputStream;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            inputStream = context.openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
        } catch (IOException e) {

        }

        return stringBuilder.toString();
    }
    //웹서버에서 file 다운
    public static void downloadFile(Context context, String fileUrl, String fileName) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            try (InputStream inputStream = new URL(fileUrl).openStream();
                 FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {

                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Optional: Wait for the download to complete
        try {
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

}

