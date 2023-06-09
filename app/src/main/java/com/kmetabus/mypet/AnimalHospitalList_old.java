package com.kmetabus.mypet;

import android.content.Context;

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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
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

public class AnimalHospitalList_old {
    //myLatitude 현재 기기 좌표, day: MONDAY,TUESDAY,WEDNESDAY,THURSDAY
    public static List<AnimalHospital>  getList( double myLatitude, double myLongitude,Context ctx, String gbn ,String petgbn ) {

        List<AnimalHospital> hospitalList = new ArrayList<>();
        try {
        	Document document = null;
            String filePath = "";
            DayOfWeek ntoday = LocalDate.now().getDayOfWeek();
            String fileUrl = DayOfWeekUrl.valueOf(ntoday.name()).getUrl(); // get file url( 월,화,수,목 )
            if("H".equals(petgbn)){ // pet 구분 ,병원
                filePath = "pet_hospital.xml";
            }
            else if("C".equals(petgbn)){ // pet 구분 , 장묘업
                filePath = "pet_memory.xml";
            }
            else if("B".equals(petgbn)){ // pet 구분 , 미용
                filePath = "pet_beauty.xml";
            }
            else if("CF".equals(petgbn)){ // pet 구분 , 카페
                filePath = "pet_cafe.xml";
            }


            if("NEW".equals(gbn) ){ // 서버에서 새로 data를 받는다.
//System.out.println("파일 존재 NEW"  );
                downloadFile(ctx, fileUrl, filePath);
            }
//System.out.println("파일 존재 기존파일 "  );
            //String filePath = "D:\\work\\pet_hospital.xml";
            String xml = "";
            FileInputStream fis = null;
            try {
                fis = ctx.openFileInput(filePath);
            }catch(Exception e){
                //System.out.println("파일 존재 FileInputStreamn null  "  );
                fileUrl = DayOfWeekUrl.valueOf(petgbn).getUrl(); // get file url( 월,화,수,목 )
                downloadFile(ctx, fileUrl, filePath);
                fis = ctx.openFileInput(filePath);
                //System.out.println("파일 존재 FileInputStreamn  fis  "  + fis);
            }
            int size = fis.available();
            //System.out.println("파일 존재 FileInputStreamn   size "+size  );
            byte[] buffer = new byte[1024];
            //fis.read(buffer); // size가 클경우 outofmemory 발생
            int bytesRead;
            StringBuilder sb = new StringBuilder();
            while ((bytesRead = fis.read(buffer)) != -1) {
                String chunk = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                sb.append(chunk);
            }
            xml = sb.toString();
           // xml = new String(buffer, StandardCharsets.UTF_8);
////System.out.println("xml"+xml);
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
                    AnimalHospital hospital = new AnimalHospital();
                    if(!isnew) {
                        ProjCoordinate wgs84Coordinate = convertUTMToWGS84(x, y);
                        //double[] latLng = getLatLngFromAddress(address);
                        //hospital = new AnimalHospital(name, phone, address, wgs84Coordinate.y, wgs84Coordinate.x, isnew, date);
                        //hospital = AnimalHospitalPool.borrowObject(name, phone, address, wgs84Coordinate.y, wgs84Coordinate.x, isnew, date, 0, 0);
                    }else{// 신규건

                        //hospital = AnimalHospitalPool.borrowObject(name, phone, address, x, y, isnew, date, myLatitude, myLongitude);
                       // hospital = new AnimalHospital(name, phone, address, x, y, isnew, date,myLatitude, myLongitude);
                    }
 ////System.out.println("cdhgold getName"+hospital.getName());
                    hospitalList.add(hospital);

                }
            }// end for

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
 ////System.out.println("cdhgold 1  nowdt "+nowdt+"    "+aLocalDate+"      "+aDaysDifference+"  "+ bLocalDate+ "    "+ bDaysDifference );
                if ((a.getIsNew()   && aDaysDifference <= 30) || ( b.getIsNew() && bDaysDifference <= 30)) {
                   // //System.out.println("cdhgold 1");
                    return -1;

                } else {
                    ////System.out.println("cdhgold 3");
                    double distanceA = a.distanceTo(myLatitude, myLongitude);
                    double distanceB = b.distanceTo(myLatitude, myLongitude);
                    return Double.compare(distanceA, distanceB);
                }

			});

        } catch (Exception e) {
            e.printStackTrace();
        }

        if("H".equals(petgbn)){ // pet 구분 ,병원
            ListViewModel.setDataList(hospitalList);
        }
        else if("C".equals(petgbn)){ // pet 구분 , 장묘업
            ListViewModel.setDataCList(hospitalList);
        }
        else if("B".equals(petgbn)){ // pet 구분 , 미용
            ListViewModel.setDataBList(hospitalList);
        }
        else if("CF".equals(petgbn)){ // pet 구분 , 카페
            ListViewModel.setDataCfList(hospitalList);
        }

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

