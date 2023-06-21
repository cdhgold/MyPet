package com.kmetabus.mypet;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity  {

    public static final String PREFERENCES_NAME = "Chkdate";
    public static final String PREFERENCE_KEY = "Newdate";
    private static final long DOUBLE_BACK_PRESS_INTERVAL = 2000; // 2초 간격으로 뒤로 가기 버튼을 눌렀을 때 종료
    private long lastBackPressTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // SharedPreferences 객체를 생성,앱이 종료되고 다시 시작되더라도 값 유지
        SharedPreferences sharedPreferences = this.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        // SharedPreferences를 이용하여 사용자의 이름을 불러옴
        String olddt = sharedPreferences.getString(PREFERENCE_KEY, "");

        Location loc = ListViewModel.getLocation();
        if(loc == null ) {
            //System.out.println("loc == null");
            Location location = getCurrentLocation();// 현위치 위도,경도
            ListViewModel.setLocation(location);
        }
        //System.out.println("loc1 == "+loc);
        // xml data변경여부확인
        String BASE_URL = "http://kmetabus.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(UnsafeOkHttpClient.getUnsafeOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<ServerResponse> call = null;
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        switch (today) {
            case TUESDAY: //TUESDAY
                call = apiService.getValueForTuesday(); // 장묘
                break;
            case WEDNESDAY:
                call = apiService.getValueForWednesday(); // 뷰티
                break;
            case THURSDAY:
                call = apiService.getValueForThursday(); // 카페
                break;
            default:
                call = apiService.getValueForMonday(); // 병원
                break;
        }
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    ServerResponse serverResponse = response.body();
                    String chkdt = serverResponse.getMessage(); // data 신규갱신일자를 가져온다.

  Log.d("cdhgold", "Vchkdt: " + chkdt+ "   olddt "+olddt);
                    if(!"".equals(chkdt)){
                        // 새로다운로드한다. - xml에 최신변경일자를 두어, 내부저장소 파일과 비교
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        // SharedPreferences를 이용하여 , 저장
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        try {
                            if(!"".equals(olddt)){
                                Date serverdt = format.parse(chkdt); // 서버에서 가져온값
                                Date clientdt = format.parse(olddt); // 앱에 저장된  가져온값
                                if (serverdt.after(clientdt)) {// data갱신
                                    editor.putString("NEW", "NEW");
                                }else{
                                    editor.putString("NEW", "");
                                }
                            }


                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        editor.putString(PREFERENCE_KEY, chkdt);
                        editor.apply();


                    }
                    Log.d("cdhgold", "Value from server: " + serverResponse.getMessage());
                } else {
                    Log.e("cdhgold", "Request failed. HTTP status code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.e("cdhgold", "Request failed: " + t.getMessage());
            }
        });

    }

    private Location getCurrentLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (lm == null) {
            return null;
        }
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( MainActivity.this, new String[] {
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, 0 );
        }
        // 가장최근 위치정보 가져오기
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null) {
            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();

            //System.out.println("Latitude1: " + latitude + ", Longitude1: " + longitude);
        }

        // 위치정보를 원하는 시간, 거리마다 갱신해준다.
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000,
                1,
                gpsLocationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                1000,
                1,
                gpsLocationListener);

        return location;
    }
    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // 위치 리스너는 위치정보를 전달할 때 호출되므로 onLocationChanged()메소드 안에 위지청보를 처리를 작업을 구현 해야합니다.
            String provider = location.getProvider();  // 위치정보
            double longitude = location.getLongitude(); // 경도
            double latitude = location.getLatitude(); // 위도
            double altitude = location.getAltitude(); // 고도
            ListViewModel.setSloc(provider);
////System.out.println("Latitude: " + latitude + ", Longitude: " + longitude);
        } public void onStatusChanged(String provider, int status, Bundle extras) {

        } public void onProviderEnabled(String provider) {

        } public void onProviderDisabled(String provider) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ListViewModel.setSloc("");   // static 변수를 null로 초기화합니다.
        ListViewModel.setLocation(null);

    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackPressTime > DOUBLE_BACK_PRESS_INTERVAL) {
            // 두 번째 뒤로 가기 버튼 클릭이 설정된 간격 내에 발생하지 않으면 토스트 메시지를 표시
            Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            lastBackPressTime = currentTime;
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            navController.navigate(R.id.mainFragment);
        } else {
            // 두 번째 뒤로 가기 버튼 클릭이 설정된 간격 내에 발생하면 앱을 종료
            super.onBackPressed();
            finishAffinity();
        }
    }

}
