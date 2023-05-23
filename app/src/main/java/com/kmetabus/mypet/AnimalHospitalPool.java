package com.kmetabus.mypet;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AnimalHospitalPool {
    private static ConcurrentLinkedQueue<AnimalHospital> pool = new ConcurrentLinkedQueue<>();

    public static AnimalHospital borrowObject(String name, String phone, String address,
                                              double distance, boolean isnew,
                                              Date date, double latitude, double longitude) {
        AnimalHospital hospital = pool.poll();

        if (hospital == null) {
            hospital = new AnimalHospital(name, phone, address, distance, isnew, date,0,0);


        } else {
            hospital.setName(name);
            hospital.setPhone(phone);
            hospital.setAddress(address);
            hospital.setIsNew(isnew);
            hospital.setToday(date);
            hospital.setDistance(distance);

        }
        return hospital;
    }

    public static void returnObject(AnimalHospital hospital) {
        // Assuming that AnimalHospital class has reset() method to reset its state
        hospital.reset();
        pool.offer(hospital);
    }
}

