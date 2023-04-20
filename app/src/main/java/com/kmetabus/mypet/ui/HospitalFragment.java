package com.kmetabus.mypet.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        //id recycler
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listAdapter = new ListAdapter(getListItems(), this);
        recyclerView.setAdapter(listAdapter);

        return view;

    }

    @Override
    public void onListItemClick(ListItem listItem) {

        NavController navController = Navigation.findNavController(requireView());

    }

    // getMenuItems() 메서드 구현...
    private List<ListItem> getListItems() {
        List<ListItem> items = new ArrayList<>();
        items.add(new ListItem("1", "mypet", "test", "H" ,"" ,0));
        items.add(new ListItem("2", "mypet", "test", "H" ,"" ,0));
        // Add more menu items as needed
        return items;
    }

}

