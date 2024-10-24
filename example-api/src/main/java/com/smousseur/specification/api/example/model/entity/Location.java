package com.smousseur.specification.api.example.model.entity;

import lombok.Data;

@Data
public class Location {
  private Integer lattitude;
  private Integer longitude;
  private String street;
  private Boolean ghetto;
  private Coord coord;
}
