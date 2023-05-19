package com.kmetabus.mypet;
// 요일별 가져올 파일명
public enum DayOfWeekFile
{
    MONDAY("pet_hospital.xml"),
    TUESDAY("pet_memory.xml"),
    WEDNESDAY("pet_beauty.xml"),
    THURSDAY("pet_cafe.xml") ;
    private String fname;

    DayOfWeekFile(String fname) {
        this.fname = fname;
    }
    public String getUrl() {
        return fname;
    }
}
