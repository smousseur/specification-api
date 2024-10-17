package com.smousseur.specification.api.service.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class TestSearchApiUser {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name;

  private String country;

  private Boolean active;

  @OneToOne
  @JoinColumn(name = "id", referencedColumnName = "users_id")
  private TestSearchApiAddress address;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public TestSearchApiAddress getAddress() {
    return address;
  }

  public void setAddress(TestSearchApiAddress address) {
    this.address = address;
  }
}
