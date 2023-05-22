package com.kmetabus.mypet;

import android.location.Location;

import androidx.lifecycle.ViewModel;

import org.w3c.dom.NodeList;

import java.util.List;
/*
 xml 정보를 파싱해 , 파싱된 결과를 담아둔다. 재사용
 */
public class ListViewModel  extends ViewModel {
    private static List<AnimalHospital> dataList; //병원
    private static NodeList hosNl; //병원
    private static NodeList cemNl; //장묘
    private static NodeList beaNl; //미용
    private static NodeList cfNl; //카페

    private static List<AnimalHospital> dataCList; //장묘
    private static List<AnimalHospital> dataBList; //미용
    private static List<AnimalHospital> dataCfList; //카페
    private static Location loc;
    private static String  sloc;

    public static List<AnimalHospital> getDataList() {
        return dataList;
    }

    public static NodeList getHosNl() {
        return ListViewModel.hosNl;
    }
    public static NodeList getCemNl() {
        return ListViewModel.cemNl;
    }
    public static NodeList getBeaNl() {
        return ListViewModel.beaNl;
    }
    public static NodeList getCfNl() {
        return ListViewModel.cfNl;
    }


    public static List<AnimalHospital> getDataCList() {
        return dataCList;
    }
    public static List<AnimalHospital> getDataBList() {
        return dataBList;
    }
    public static List<AnimalHospital> getDataCfList() {
        return dataCfList;
    }

    public static void setDataList(List<AnimalHospital> list) {
        dataList = list;
    }
    public static void setHosNl(NodeList nl) {
        ListViewModel.hosNl = nl;
    }
    public static void setCemNl(NodeList nl) {
        ListViewModel.cemNl = nl;
    }
    public static void setBeaNl(NodeList nl) {
        ListViewModel.beaNl = nl;
    }
    public static void setCfNl(NodeList nl) {
        ListViewModel.cfNl = nl;
    }
    public static void setDataCList(List<AnimalHospital> list) {
        dataCList = list;
    }
    public static void setDataBList(List<AnimalHospital> list) {
        dataBList = list;
    }
    public static void setDataCfList(List<AnimalHospital> list) {
        dataCfList = list;
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