package com.smousseur.specification.api.example.model.entity;

import com.smousseur.specification.api.example.model.entity.converter.LocationConverter;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "address")
@Data
public class Address {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "country")
  private String country;

  @Column(name = "city")
  private String city;

  @Column(name = "location")
  @Convert(converter = LocationConverter.class)
  private Location location;
}
