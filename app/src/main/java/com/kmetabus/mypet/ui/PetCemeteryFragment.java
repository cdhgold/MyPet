package com.kmetabus.mypet.ui;

import static com.kmetabus.mypet.MainActivity.PREFERENCES_NAME;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kmetabus.mypet.AnimalHospital;
import com.kmetabus.mypet.AnimalHospitalList;
import com.kmetabus.mypet.EndlessRecyclerOnScrollListener;
import com.kmetabus.mypet.ListViewModel;
import com.kmetabus.mypet.R;

import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
동물장묘
 */
public class PetCemeteryFragment extends Fragment implements OnListItemClickListener{


        private RecyclerView recyclerView;
        private ListAdapter listAdapter;
        private Double lat = 0.0;
        private Double logi = 0.0;
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            //layout
            Context ctx = getActivity();
            List<AnimalHospital> list = null;
            View view = inflater.inflate(R.layout.fragment_petcemetery, container, false);
            Location location = ListViewModel.getLocation();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
            // SharedPreferences를 이용하여 사용자의 이름을 불러옴
            String xmlnew = sharedPreferences.getString("NEW", "");
            NodeList nl = ListViewModel.getCemNl();
//System.out.println("location "+ location);
            if (location != null) {
                // 위치 데이터를 처리하는 로직을 구현
                Double lat = location.getLatitude();
                Double logi = location.getLongitude();
            }
            //id recycler
            Context ctx2 = ctx.getApplicationContext();
            if (nl == null || nl.getLength() == 0) {
                nl = AnimalHospitalList.getList(lat, logi, ctx2, xmlnew, "C"); // data를 가져온다
            }
System.out.println("nl length "+ nl.getLength() );
            ListViewModel.setCemNl(nl);
            recyclerView = view.findViewById(R.id.cemetery_recyclerview);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            List<AnimalHospital> hospitalList = AnimalHospitalList.getlistCount( nl ,  lat,   logi, "C",0);
            listAdapter = new ListAdapter( getListItems(hospitalList, 0), this);
            recyclerView.setAdapter(listAdapter);
            recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager,listAdapter, lat,   logi, "C" ) {
                @Override
                public void loadMoreItems() {

                }
            });

            return view;

        }

        @Override
        public void onListItemClick(View v, ListItem listItem) {
            Location loc = listItem.getLoc();
            String label = listItem.getCol1();// 병원명  마커에 표시할 라벨
            String lat = String.valueOf(loc.getLatitude());
            String logi = String.valueOf(loc.getLongitude());
            //NavController navController = Navigation.findNavController(requireView());
            String location = lat+","+ logi; // 병원 위치
            Uri gmmIntentUri = Uri.parse("geo:" + location + "?z=12&q=" + location + "(" + label + ")"); // Uri 객체 생성
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri); // Intent 객체 생성
            mapIntent.setPackage("com.google.android.apps.maps");

            v.getContext().startActivity(mapIntent);

        }

        // getMenuItems() 메서드 구현...
        public static List<ListItem> getListItems(List<AnimalHospital> list,int i ) {
            List<ListItem> items = new ArrayList<>();

            for (AnimalHospital hospital : list) {
                String addr = hospital.getAddress();
                Double dist = hospital.getDistance();
                long idist = Math.round(dist) ;
                String locprovier =ListViewModel.getSloc();
                Location loc = new Location(locprovier);
                loc.setLatitude(hospital.getLatitude());
                loc.setLongitude(hospital.getLongitude());
                boolean isNew = hospital.getIsNew();
                Date today = hospital.getToday();

                i++;
                items.add(new ListItem(i+"", hospital.getName(), hospital.getPhone(), addr ,idist+" km",0, loc,isNew, today ));

            }
            // Add more menu items as needed
            return items;
        }
}

