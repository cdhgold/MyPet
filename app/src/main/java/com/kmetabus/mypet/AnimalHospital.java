package com.kmetabus.mypet;

import java.util.Date;

public class AnimalHospital {
    private String name;
    private String phone;
    private String address;
    private double latitude;
    private double longitude;
	private double distance;
	private boolean isNew; // 유료결재시 한달간 top에 링크
	private Date today;
	public AnimalHospital(){

	}
	public AnimalHospital(String name, String phone, String address, double latitude, double longitude,boolean isnew, Date today,double nlati, double nlogi) {
		this.name = name;
		this.phone = phone;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		if(nlati == 0){ // 이전건
			distanceTo(this.latitude,this.longitude);
		}else { //신규건
			distanceTo(nlati, nlogi);
		}
		this.isNew = isnew;
		this.today = today;
	}
	public String getName() {
		return name;
	}
	public String getPhone() {
		return phone;
	}
	public String getAddress() {
		return address;
	}
	public double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public double getDistance() {
		return distance;
	}
	public boolean getIsNew() {
		return isNew;
	}
	public Date getToday() {
		return today;
	}
	public void setName(String param) {
		this.name=param;
	}
	public void setPhone(String param) {
		this.phone=param;
	}
	public void setAddress(String param) {
		this.address=param;
	}
	public void setLatitude(double param) {
		this.latitude=param;
	}
	public void setLongitude(double param) {
		this.longitude=param;
	}
	public void setDistance(double param) {
		this.distance=param;
	}
	public void setIsNew(boolean param) {
		this.isNew=param;
	}
	public void setToday(Date param) {
		this.today=param;
	}
	public double distanceTo(double lat, double lng) {
		double earthRadius = 6371; // 지구의 평균 반지름 (km)
		double dLat = Math.toRadians(lat - this.latitude);
		double dLng = Math.toRadians(lng - this.longitude);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
				   Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(lat)) *
				   Math.sin(dLng / 2) * Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		this.distance = earthRadius * c;
		return distance;
	}
	public void reset(){
		this.name = null;
		this.phone = null;
		this.address = null;
		this.latitude = 0;
		this.longitude = 0;
		this.distance = 0;
		this.isNew = false;
	}
}