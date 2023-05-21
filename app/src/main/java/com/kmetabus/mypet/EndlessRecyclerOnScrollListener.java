package com.kmetabus.mypet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kmetabus.mypet.ui.HospitalFragment;
import com.kmetabus.mypet.ui.ListAdapter;

import org.w3c.dom.NodeList;

import java.util.List;

public class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    private static final int VISIBLE_THRESHOLD = 10;// 사용자가 리스트의 맨 밑에서 다섯 번째 아이템까지 도달했을 때 새로운 아이템을 로
    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;

    private LinearLayoutManager mLinearLayoutManager;
    private ListAdapter mAdapter;  // add this line
    double lati = 0.0;
    double longi = 0.0;
    String petgbn = "";
    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager, ListAdapter adapter,double lati, double longi,String petgbn ) {
        this.mLinearLayoutManager = linearLayoutManager;
        this.mAdapter = adapter;  // and this line
        this.lati = lati;
        this.longi = longi;
        this.petgbn = petgbn;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        pastVisibleItems = mLinearLayoutManager.findFirstVisibleItemPosition();
System.out.println("visibleItemCount "+visibleItemCount);
System.out.println("totalItemCount "+totalItemCount);
System.out.println("pastVisibleItems"+pastVisibleItems);

        //if (!loading) {
            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                loading = true;
                //Do pagination.. i.e. fetch new data
                loadMoreItems();
            }
        //}
    }

    public void loadMoreItems(){
        NodeList nodeList= ListViewModel.getHosNl();
  System.out.println("loadMoreItems   getLength  "+nodeList.getLength());
        List<AnimalHospital> hospitalList = AnimalHospitalList.getlistCount( nodeList ,  lati,   longi, petgbn,totalItemCount);
        System.out.println("ListViewModel hospitalList " + hospitalList.size());
        mAdapter.addItems(HospitalFragment.getListItems(hospitalList) );
        mAdapter.notifyDataSetChanged();
    }

    public void setLoaded() {
        loading = false;
    }

}

