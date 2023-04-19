package com.kmetabus.mypet;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMenuItemClickListener {

    private RecyclerView menuRecyclerView;
    private MenuAdapter menuAdapter;
    private List<MenuItem> menuItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
    }

    private List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        items.add(new MenuItem("동물병원", "mypet", R.drawable.hospital, "H" ));
        items.add(new MenuItem("동물장묘업", "mypet",R.drawable.god,     "G"));
        items.add(new MenuItem("동물미용실", "mypet",R.drawable.mypet,   "T"));
        items.add(new MenuItem("반려동물카페", "mypet",R.drawable.mypet, "T"));
        // Add more menu items as needed
        return items;
    }
    @Override
    public void onMenuItemClick(MenuItem menuItem) {
        // 여기에서 새 화면을 시작하십시오.
        Log.e("test ", menuItem.getTitle() );
        String gbn = menuItem.getGbn();
        // Get the NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // Navigate to the appropriate fragment based on "gbn" value
        if("H".equals(gbn)){        // 동물병원
            navController.navigate(R.id.hospitalFragment);
        }
        else if("G".equals(gbn)){   // 동물장묘업
           // navController.navigate(R.id.animalCemeteryFragment);
        }
        else if("T".equals(gbn)){   // 동물미용실
           // navController.navigate(R.id.petSalonFragment);
        }
        else if("C".equals(gbn)){   // 반려동물카페
           // navController.navigate(R.id.petCafeFragment);
        }


    }

}
