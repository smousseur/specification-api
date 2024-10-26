package com.smousseur.specification.api.service.model;

import com.smousseur.specification.api.annotation.SearchPath;
import com.smousseur.specification.api.annotation.SearchRequestObject;

@SearchRequestObject
public class TestSearchApiModel {
  @SearchPath("property(name, string) like ?")
  private String name;

  @SearchPath("property(country, string) = ?")
  private String country;

  @SearchPath("property(active, bool) = ?")
  private Boolean active;

  @SearchPath("join(address)->property(latitude, long) = ?")
  private Long latitude;

  @SearchPath("join(address)->property(altitude, float) = ?")
  private Float altitude;

  @SearchPath("join(address)->property(id, int) = ?")
  private Integer addressId;

  @SearchPath("join(address)->property(street, string) = ?")
  private String street;

  @SearchPath("join(address)->property(temperature, double) = ?")
  private Double temperature;

  public void setName(String name) {
    this.name = name;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public void setLatitude(Long latitude) {
    this.latitude = latitude;
  }

  public String getName() {
    return name;
  }

  public String getCountry() {
    return country;
  }

  public Boolean getActive() {
    return active;
  }

  public Long getLatitude() {
    return latitude;
  }

  public Float getAltitude() {
    return altitude;
  }

  public void setAltitude(Float altitude) {
    this.altitude = altitude;
  }

  public Integer getAddressId() {
    return addressId;
  }

  public void setAddressId(Integer addressId) {
    this.addressId = addressId;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public Double getTemperature() {
    return temperature;
  }

  public void setTemperature(Double temperature) {
    this.temperature = temperature;
  }
}
