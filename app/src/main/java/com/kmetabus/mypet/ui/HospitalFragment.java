package com.kmetabus.mypet.ui;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kmetabus.mypet.AnimalHospital;
import com.kmetabus.mypet.AnimalHospitalList;
import com.kmetabus.mypet.MainActivity;
import com.kmetabus.mypet.MenuAdapter;
import com.kmetabus.mypet.MenuItem;
import com.kmetabus.mypet.OnMenuItemClickListener;
import com.kmetabus.mypet.R;

import java.util.ArrayList;
import java.util.List;

public class HospitalFragment extends Fragment implements OnListItemClickListener {

    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //layout
        Context ctx = getActivity();
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        Bundle locationBundle = getArguments();
        if (locationBundle != null) {
            Location location = locationBundle.getParcelable("location");
            if (location != null) {
                // 위치 데이터를 처리하는 로직을 구현
                Double lat = location.getLatitude();
                Double logi = location.getLongitude();
                //id recycler
                Context ctx2 = ctx.getApplicationContext();
                List<AnimalHospital> list = AnimalHospitalList.getList(lat, logi,ctx2 ); // data를 가져온다
                recyclerView = view.findViewById(R.id.recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                listAdapter = new ListAdapter(getListItems(list), this);
                recyclerView.setAdapter(listAdapter);

            }
        }
        return view;

    }

    @Override
    public void onListItemClick(ListItem listItem) {

        NavController navController = Navigation.findNavController(requireView());

    }

    // getMenuItems() 메서드 구현...
    private List<ListItem> getListItems(List<AnimalHospital> list) {
        List<ListItem> items = new ArrayList<>();
        int i = 0;
        for (AnimalHospital hospital : list) {
            String addr = hospital.getAddress();
            Double dist = hospital.getDistance();
            i++;
            items.add(new ListItem(i+"", hospital.getName(), hospital.getPhone(), addr ,dist+" km",0));

        }
        // Add more menu items as needed
        return items;
    }

}

