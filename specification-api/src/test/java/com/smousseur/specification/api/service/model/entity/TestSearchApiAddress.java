package com.smousseur.specification.api.service.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "address")
public class TestSearchApiAddress {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String street;

  private Long latitude;

  private Long longitude;

  private Float altitude;

  private Double temperature;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public Long getLatitude() {
    return latitude;
  }

  public void setLatitude(Long latitude) {
    this.latitude = latitude;
  }

  public Long getLongitude() {
    return longitude;
  }

  public void setLongitude(Long longitude) {
    this.longitude = longitude;
  }

  public Float getAltitude() {
    return altitude;
  }

  public void setAltitude(Float altitude) {
    this.altitude = altitude;
  }

  public Double getTemperature() {
    return temperature;
  }

  public void setTemperature(Double temperature) {
    this.temperature = temperature;
  }
}
