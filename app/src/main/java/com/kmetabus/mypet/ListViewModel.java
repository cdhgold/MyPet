package com.kmetabus.mypet;

import android.location.Location;

import androidx.lifecycle.ViewModel;

import java.util.List;
/*
 xml 정보를 파싱해 , 파싱된 결과를 담아둔다. 재사용
 */
public class ListViewModel  extends ViewModel {
    private static List<AnimalHospital> dataList;
    private static Location loc;

    public static List<AnimalHospital> getDataList() {
        return dataList;
    }

    public static void setDataList(List<AnimalHospital> dataList) {
        dataList = dataList;
    }

    public static Location getLocation() {
        return loc;
    }

    public static void setLocation(Location  ploc) {
        loc = ploc;
    }

}