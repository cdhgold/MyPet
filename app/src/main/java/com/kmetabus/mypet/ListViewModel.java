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
    private static String  sloc;

    public static List<AnimalHospital> getDataList() {
        return dataList;
    }

    public static void setDataList(List<AnimalHospital> list) {

        dataList = list;
    }

    public static Location getLocation() {
        return loc;
    }

    public static void setLocation(Location  ploc) {
        loc = ploc;
    }
    public static String getSloc() {
        return sloc;
    }
    public static void setSloc(String tloc) {
        sloc = tloc;

    }

}