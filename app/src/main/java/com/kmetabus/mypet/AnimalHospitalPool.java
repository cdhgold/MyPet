package com.kmetabus.mypet;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AnimalHospitalPool {
    private static ConcurrentLinkedQueue<AnimalHospital> pool = new ConcurrentLinkedQueue<>();

    public static AnimalHospital borrowObject(String name, String phone, String address,
                                              double x, double y, boolean isnew,
                                              Date date, double latitude, double longitude) {
        AnimalHospital hospital = pool.poll();

        if (hospital == null) {
            if(!isnew){
                hospital = new AnimalHospital(name, phone, address, x, y, isnew, date,0,0);
            }else{
                //신규건
                hospital = new AnimalHospital(name, phone, address, x, y, isnew, date, latitude, longitude);
            }

        } else {
            hospital.setName(name);
            hospital.setPhone(phone);
            hospital.setAddress(address);
            hospital.setIsNew(isnew);
            hospital.setToday(date);

            if(!isnew){
                hospital.distanceTo(x,y);
                hospital.setLatitude(x);
                hospital.setLongitude(y);
            }else{
                //신규
                hospital.distanceTo(latitude,longitude);
                hospital.setLatitude(x);
                hospital.setLongitude(y);
            }
        }
        return hospital;
    }

    public static void returnObject(AnimalHospital hospital) {
        // Assuming that AnimalHospital class has reset() method to reset its state
        hospital.reset();
        pool.offer(hospital);
    }
}

