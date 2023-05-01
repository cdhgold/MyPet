package com.kmetabus.mypet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
/*
메뉴 fragment
 */
public class MainFragment extends Fragment implements OnMenuItemClickListener {

    private RecyclerView recyclerView;
    private MenuAdapter menuAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        menuAdapter = new MenuAdapter(getMenuItems(), this);
        recyclerView.setAdapter(menuAdapter);

        return view;
    }

    @Override
    public void onMenuItemClick(MenuItem menuItem) {
        String gbn = menuItem.getGbn();
        NavController navController = Navigation.findNavController(requireView());

        // Navigate to the appropriate fragment based on "gbn" value
        if ("H".equals(gbn)) { // 동물병원
            navController.navigate(R.id.action_menuFragment_to_hospitalFragment);
        } else if ("G".equals(gbn)) { // 동물장묘업
            navController.navigate(R.id.action_mainFragment_to_petCemeteryFragment);
        } else if ("01".equals(gbn)) { // 동물미용실
            navController.navigate(R.id.beauty);
        } else if ("02".equals(gbn)) { // 반려동물카페
            navController.navigate(R.id.cafe);
        }
    }

    // getMenuItems() 메서드 구현...
    private List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        items.add(new MenuItem("동물병원", "mypet", R.drawable.hospital, "H" ));
        items.add(new MenuItem("동물장묘업", "mypet",R.drawable.god,     "G"));
        items.add(new MenuItem("동물미용실", "mypet",R.drawable.mypet,   "01"));
        items.add(new MenuItem("반려동물카페", "mypet",R.drawable.mypet, "02"));
        // Add more menu items as needed
        return items;
    }
}
