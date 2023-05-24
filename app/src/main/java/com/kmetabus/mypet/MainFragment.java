package com.kmetabus.mypet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
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
        showProgressBar();

        // Navigate to the appropriate fragment based on "gbn" value
        if ("H".equals(gbn)) { // 동물병원
            //navController.navigate(R.id.action_menuFragment_to_hospitalFragment);
            navController.navigate(R.id.action_menuFragment_to_hospitalFragment);
            progress(navController);
        } else if ("HN".equals(gbn)) { // 동물병원 신규등록
            Intent intent = new Intent(getActivity(), HosInActivity.class);
            startActivity(intent);
            hideProgressBar();
        } else if ("G".equals(gbn)) { // 동물장묘업
            navController.navigate(R.id.action_mainFragment_to_petCemeteryFragment);
            progress(navController);
        } else if ("01".equals(gbn)) { // 동물미용실
            navController.navigate(R.id.action_beauty);
            progress(navController);
        } else if ("02".equals(gbn)) { // 반려동물카페
            navController.navigate(R.id.action_cafe);
            progress(navController);
        }
    }
    private void progress(NavController navController ){
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.hospitalFragment
                        ||  destination.getId() == R.id.petCemeteryFragment
                        ||  destination.getId() == R.id.beauty
                        ||  destination.getId() == R.id.cafe
                ) {
                    controller.removeOnDestinationChangedListener(this);
                    getView().post(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressBar();
                        }
                    });
                }
            }
        });


    }
    // getMenuItems() 메서드 구현...
    private List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        items.add(new MenuItem("동물병원", "mypet", R.drawable.hospital, "H" ));
        items.add(new MenuItem("동물장묘업", "mypet",R.drawable.god,     "G"));
        items.add(new MenuItem("동물미용실", "mypet",R.drawable.pet_beauty,   "01"));
        items.add(new MenuItem("반려동물카페", "mypet",R.drawable.pet_cafe, "02"));
        items.add(new MenuItem("", "", R.drawable.pet_new, "HN" ));

        // Add more menu items as needed
        return items;
    }
    public void showProgressBar() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.progress_layout, null);
        view.setTag("ProgressBar");

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );

        getActivity().addContentView(view, layoutParams);
    }
    public void hideProgressBar() {
        View view = getActivity().getWindow().getDecorView().findViewWithTag("ProgressBar");
        if (view != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }
}
