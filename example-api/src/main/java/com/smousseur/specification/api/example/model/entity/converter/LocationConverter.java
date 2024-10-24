package com.smousseur.specification.api.example.model.entity.converter;

import com.smousseur.specification.api.example.model.entity.Location;

public class LocationConverter extends JsonConverter<Location> {
  @Override
  protected Class<Location> getClazz() {
    return Location.class;
  }
}
