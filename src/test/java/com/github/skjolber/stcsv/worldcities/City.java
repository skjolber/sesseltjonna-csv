package com.github.skjolber.stcsv.worldcities;

import com.univocity.parsers.annotations.Parsed;

public class City {

    @Parsed
    private String country;
    @Parsed
    private String city;
    @Parsed
    private String accentCity;
    @Parsed
    private String region;
    @Parsed
    private long population;
    @Parsed
    private double latitude;
    @Parsed
    private double longitude;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAccentCity() {
        return accentCity;
    }

    public void setAccentCity(String accentCity) {
        this.accentCity = accentCity;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public long getPopulation() {
        return population;
    }

    public void setPopulation(long population) {
        this.population = population;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "City{" +
                "country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", accentCity='" + accentCity + '\'' +
                ", region='" + region + '\'' +
                ", population=" + population +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
