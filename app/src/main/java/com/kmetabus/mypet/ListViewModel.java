package com.kmetabus.mypet;

import androidx.lifecycle.ViewModel;

import java.util.List;
/*
 xml 정보를 파싱해 , 파싱된 결과를 담아둔다. 재사용
 */
public class ListViewModel  extends ViewModel {
    private List<AnimalHospital> dataList;

    public List<AnimalHospital> getDataList() {
        return dataList;
    }

    public void setDataList(List<AnimalHospital> dataList) {
        this.dataList = dataList;
    }
}