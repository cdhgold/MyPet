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
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    //myLatitude 현재 기기 좌표, day: MONDAY,TUESDAY,WEDNESDAY,THURSDAY
    public static NodeList getList( double myLatitude, double myLongitude,Context ctx, String gbn ,String petgbn ) {
        NodeList nodeList = null;
        NodeList sortedNodeList = null;
        try {
        	Document document = null;
            String filePath = "";
            String fileUrl = DayOfWeekUrl.valueOf(petgbn).getUrl(); // get file url
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
                ////System.out.println("파일 존재 FileInputStreamn null  "  );
                downloadFile(ctx, fileUrl, filePath);
                fis = ctx.openFileInput(filePath);
                ////System.out.println("파일 존재 FileInputStreamn  fis  "  + fis);
            }
            int size = fis.available();
            ////System.out.println("파일 존재 FileInputStreamn   size "+size  );
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
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            //Document document = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            //Document document = builder.parse(filePath);
            InputSource inputSource = new InputSource(new StringReader(xml));
            document = builder.parse(inputSource);
            
            document.getDocumentElement().normalize();

            nodeList = document.getElementsByTagName("row");
            List<Node> nodes = new ArrayList<>();

            for (int i = 0; i < nodeList.getLength(); i++) {
                String isNew = null,chkempty = "";
                Node node = nodeList.item(i);
                Double distance1 = 0.0 , lat1 = 0.0 , lon1 = 0.0;
                // Document를 얻는다
                Document doc1 = node.getOwnerDocument();
                // dist라는 새 Element를 생성한다
                Element distElement1 = doc1.createElement("dist");
                if( (((Element) node).getElementsByTagName("x").item(0).getTextContent().trim()).length() >0 ) {
                    lat1 = Double.parseDouble(((Element) node).getElementsByTagName("x").item(0).getTextContent().trim());
                }else{
                    chkempty = "Y"; // 좌표가 없음
                }
                if( (((Element) node).getElementsByTagName("y").item(0).getTextContent().trim()).length() >0 ) {
                    lon1 = Double.parseDouble(((Element) node).getElementsByTagName("y").item(0).getTextContent().trim());
                }else{
                    chkempty = "Y";
                }
                if( ((Element) node).getElementsByTagName("isNew").getLength() >0 ) {
                    isNew = ((Element) node).getElementsByTagName("isNew").item(0).getTextContent().trim();
                }
//System.out.println("AnimalHospialList  isNew " + Boolean.parseBoolean(isNew)+ " chkempty "+chkempty);
                if(Boolean.parseBoolean(isNew)){ // 신규건 위도,경도
                    distance1 = haversineDistance(myLatitude, myLongitude, lat1, lon1);
                }else{
                    if("".equals(chkempty)) {
                        ProjCoordinate wgs84Coordinate1 = convertUTMToWGS84(lat1, lon1);
                        distance1 = haversineDistance(myLatitude, myLongitude, wgs84Coordinate1.y, wgs84Coordinate1.x);
                    }else{
                        distance1 = 0.0;
                    }
                }
                distElement1.setTextContent(String.valueOf(distance1));
 //System.out.println("AnimalHospialList  distance1 " + String.valueOf(distance1));
                // node1에 dist Element를 추가한다
                node.appendChild(distElement1);
                nodes.add(node);
            }

            // 노드를 현재 위치로부터의 거리에 따라 정렬합니다.
            //지구 위의 좌표(경도와 위도)를 사용한다면, 거리 계산에는 "하버사인 공식"을 사용
            Collections.sort(nodes, new Comparator<Node>() {
                @Override
                public int compare(Node node1, Node node2) {
                    String isNew1 = "false", isNew2 = "false";
// 첫 번째 객체가 두 번째 객체보다 작으면 음의 정수, 같으면 0, 크면 양의 정수
                    String dist1 = ((Element) node1).getElementsByTagName("dist").item(0).getTextContent().trim();
                    String dist2 = ((Element) node2).getElementsByTagName("dist").item(0).getTextContent().trim();
//System.out.println("nodes dist1 "+ dist1+ " dist2 "+dist2);
                    if( ((Element) node1).getElementsByTagName("isNew").getLength() >0 ) {
                        isNew1 = ((Element) node1).getElementsByTagName("isNew").item(0).getTextContent().trim();
                    }
                    if( ((Element) node2).getElementsByTagName("isNew").getLength() >0 ) {
                        isNew2 = ((Element) node2).getElementsByTagName("isNew").item(0).getTextContent().trim();
                    }
                    if ("0.0".equals(dist1) || "".equals(dist1)) {
                        dist1 = "500";
                    }
                    if ("0.0".equals(dist2) || "".equals(dist2)) {
                        dist2 = "500";
                    }
                    boolean bIsNew1 = Boolean.parseBoolean(isNew1);
                    boolean bIsNew2 = Boolean.parseBoolean(isNew2);

                    if (bIsNew1 && !bIsNew2) {
                        return -1;
                    } else if (!bIsNew1 && bIsNew2) {
                        return 1;
                    } else {
                        return Double.compare(Double.parseDouble(dist1), Double.parseDouble(dist2));
                    }


                }
            });

            sortedNodeList = new ModifiableNodeList(nodes);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return sortedNodeList;
    }
    public static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        // 지구의 반지름(km)
        final int R = 6371;

        // 위도와 경도를 라디안 단위로 변환
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // 하버사인 공식
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c;
    }
    // scroll에 따른 100건씩 return list
    public  static List<AnimalHospital>  getlistCount(NodeList nodeList ,double myLatitude, double myLongitude,String petgbn,int start){
        List<AnimalHospital> hospitalList = new ArrayList<>();
        try{
            Date date = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            int iend = start > nodeList.getLength() ? nodeList.getLength() :  start+100 ;
            if(iend > nodeList.getLength())iend = nodeList.getLength() ;
            int is = start;
            if(start > iend )start = iend;
//System.out.println("start==> iend"+iend + "  start  "+start);
            for ( start = is ; start <= iend - 1  ; start++) { //nodeList.getLength()
                Node node = nodeList.item(start);
                //if(node == null)continue;
//System.out.println("start==> "+start);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String isNew = null;
                    String today = null;
                    String name = element.getElementsByTagName("bplcNm").item(0).getTextContent();
                    String dist = element.getElementsByTagName("dist").item(0).getTextContent();
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
                        sx = "0";
                    }
                    if("".equals(sy) ) {
                        sy = "0";
                    }
                    double x = Double.parseDouble(sx);
                    double y = Double.parseDouble(sy);
                    boolean isnew = Boolean.parseBoolean(isNew);
                    AnimalHospital hospital = new AnimalHospital();
 //System.out.println("cdhgold dist==>"+dist);
                    hospital = AnimalHospitalPool.borrowObject(name, phone, address, Double.parseDouble(dist) , isnew, date, 0, 0);

                    hospitalList.add(hospital);
                }
            }// end for

 ////System.out.println("cdhgold hospitalList.size()"+hospitalList.size() );
            // isNew는 30일간만 유효
            AtomicReference<Date> atodt = new AtomicReference<>();
            AtomicReference<Date> btodt = new AtomicReference<>();
            LocalDate nowdt = LocalDate.now();
            /*
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

                    return -1;

                } else {
                    ////System.out.println("cdhgold 3");
                    double distanceA = a.distanceTo(myLatitude, myLongitude);
                    double distanceB = b.distanceTo(myLatitude, myLongitude);
                    return Double.compare(distanceA, distanceB);
                }

            });
            */
        } catch (Exception e) {
            e.printStackTrace();
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

