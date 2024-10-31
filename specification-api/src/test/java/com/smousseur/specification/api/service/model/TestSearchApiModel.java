package com.smousseur.specification.api.service.model;

import com.smousseur.specification.api.annotation.PredicateDef;
import com.smousseur.specification.api.annotation.SpecificationDef;

@SpecificationDef
public class TestSearchApiModel {
  @PredicateDef("property(name) like ?")
  private String name;

  @PredicateDef("property(country) = ?")
  private String country;

  @PredicateDef("property(active) = ?")
  private Boolean active;

  @PredicateDef("join(address)->property(latitude) != ?")
  private Long latitude;

  @PredicateDef("join(address)->property(altitude) = ?")
  private Float altitude;

  @PredicateDef("join(address)->property(id) = ?")
  private Integer addressId;

  @PredicateDef("join(address)->property(street) = ?")
  private String street;

  @PredicateDef("join(address)->property(temperature) = ?")
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
