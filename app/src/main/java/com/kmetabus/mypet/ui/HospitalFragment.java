package com.kmetabus.mypet.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kmetabus.mypet.AnimalHospital;
import com.kmetabus.mypet.AnimalHospitalList;
import com.kmetabus.mypet.ListViewModel;
import com.kmetabus.mypet.MainActivity;
import com.kmetabus.mypet.MenuAdapter;
import com.kmetabus.mypet.MenuItem;
import com.kmetabus.mypet.OnMenuItemClickListener;
import com.kmetabus.mypet.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HospitalFragment extends Fragment implements OnListItemClickListener {

    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //layout
        Context ctx = getActivity();
        List<AnimalHospital> list = null;
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        Location location = ListViewModel.getLocation();

 System.out.println("location "+ location);
        if (location != null) {
                // 위치 데이터를 처리하는 로직을 구현
                Double lat = location.getLatitude();
                Double logi = location.getLongitude();
                //id recycler
                Context ctx2 = ctx.getApplicationContext();
                //ListViewModel listanHospotal = new ViewModelProvider(this).get(ListViewModel.class);
                list = ListViewModel.getDataList();
                if(list == null || list.size() == 0 ) {

                    System.out.println("ListViewModel 여기왔나 "+list);
                    list = AnimalHospitalList.getList(lat, logi, ctx2 ); // data를 가져온다
                    ListViewModel.setDataList(list);
                }
System.out.println("ListViewModel"+list.size());

                recyclerView = view.findViewById(R.id.recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                listAdapter = new ListAdapter(getListItems(list), this);
                recyclerView.setAdapter(listAdapter);


        }
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
    private List<ListItem> getListItems(List<AnimalHospital> list) {
        List<ListItem> items = new ArrayList<>();
        int i = 0;
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

