package com.smousseur.specification.api.service.model;

import com.smousseur.specification.api.annotation.SearchPath;
import com.smousseur.specification.api.annotation.SearchRequestObject;

@SearchRequestObject
public class TestSearchApiModel {
  @SearchPath("value(name, like(%s))")
  private String name;

  @SearchPath("value(country, eq(%s))")
  private String country;

  @SearchPath("value(active, eq(%s), bool)")
  private Boolean active;

  @SearchPath("join(address)->value(latitude, eq(%s), long)")
  private Long latitude;

  @SearchPath("join(address)->value(altitude, eq(%s), float)")
  private Float altitude;

  @SearchPath("join(address)->value(id, eq(%s), int)")
  private Integer addressId;

  @SearchPath("join(address)->value(street, eq(%s), string)")
  private String street;

  @SearchPath("join(address)->value(temperature, eq(%s), double)")
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
