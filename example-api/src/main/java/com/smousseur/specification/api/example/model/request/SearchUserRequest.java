package com.smousseur.specification.api.example.model.request;

import com.smousseur.specification.api.annotation.PredicateDef;
import com.smousseur.specification.api.annotation.PredicateId;
import com.smousseur.specification.api.annotation.SpecificationDef;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Tag this class as a entry point to generate JPA specifications. */
@Data
@NoArgsConstructor
@SpecificationDef("(pName && pAddress or pStreet) && pCity")
public class SearchUserRequest {
  @PredicateId("pName")
  @PredicateDef("property(name) like ?")
  private String name;

  @PredicateId("pAddress")
  @PredicateDef("join(address)->json_property(location, coord.x) = ?")
  private Integer lattitude;

  @PredicateId("pStreet")
  @PredicateDef("join(address)->json_property(location, street) like ?")
  private String street;

  @PredicateDef("property(lastConnection) >= ?")
  private LocalDateTime lastConnection;

  @PredicateId("pLastVisit")
  @PredicateDef("join(address)->json_property(location, lastVisit) < ?")
  private LocalDate lastVisit;

  @PredicateId("pAlias")
  @PredicateDef("property(aliases) contains ?")
  private String alias;

  @PredicateDef("join(address)->property(city) in ?")
  private List<String> cities;

  @PredicateId("pCity")
  @PredicateDef("property(city) isnull")
  private Void city;

  @PredicateDef("property(email) != ?")
  private String email;
}
