package com.kmetabus.mypet;
// 요일별 get url 정의   TUESDAY
public enum DayOfWeekUrl {
    MONDAY("http://kmetabus.com/cdh/data/pet_hospital.xml"),
    H("http://kmetabus.com/cdh/data/pet_hospital.xml"),
    TUESDAY("http://kmetabus.com/cdh/data/pet_memory.xml"),
    C("http://kmetabus.com/cdh/data/pet_memory.xml"),
    WEDNESDAY("http://kmetabus.com/cdh/data/pet_beauty.xml"),
    B("http://kmetabus.com/cdh/data/pet_beauty.xml"),
    THURSDAY("http://kmetabus.com/cdh/data/pet_cafe.xml"),
    FRIDAY(""),
    SATURDAY(""),
    SUNDAY(""),
    CF("http://kmetabus.com/cdh/data/pet_cafe.xml") ;

    private String url;

    DayOfWeekUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }
}
