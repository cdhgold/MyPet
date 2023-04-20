package com.kmetabus.mypet;

public class AnimalHospital {
    private String name;
    private String phone;
    private String address;
    private double latitude;
    private double longitude;
	private double distance;

    public AnimalHospital(String name, String phone, String address, double latitude, double longitude) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.latitude = latitude;
		this.longitude = longitude;
		distanceTo(this.latitude,this.longitude);
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

	public double distanceTo(double lat, double lng) {
		double earthRadius = 6371; // ������ ������ (km)
		double dLat = Math.toRadians(lat - this.latitude);
		double dLng = Math.toRadians(lng - this.longitude);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
				   Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(lat)) *
				   Math.sin(dLng / 2) * Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		this.distance = earthRadius * c;
		return distance;
	}
}