package com.smousseur.specification.api.example.model.request;

import com.smousseur.specification.api.annotation.PredicateDef;
import com.smousseur.specification.api.annotation.SpecificationDef;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Tag this class as a entry point to generate JPA specifications.
 *
 * @param name Perform a search using User.name like '%name%'
 * @param lattitude Perform a search using User.address.location json field and $.coord.x =
 *     lattitude
 * @param street Perform a search using User.address.location json field and $.street like
 *     '%street%'
 * @param lastConnection Perform a search using User.lastConnection > lastConnection
 * @param lastVisit Perform a search using User.address.location json field and $.lastVisit <
 *     lastVisit
 */
@SpecificationDef("(pName or pAddress) and (pStreet or pAlias)")
public record SearchUserRequest(
    @PredicateDef(id = "pName", value = "property(name) like ?") String name,
    @PredicateDef(id = "pAddress", value = "join(address)->json_property(location, coord.x) = ?")
        Integer lattitude,
    @PredicateDef(id = "pStreet", value = "join(address)->json_property(location, street) like ?")
        String street,
    @PredicateDef("property(lastConnection) >= ?") LocalDateTime lastConnection,
    @PredicateDef(
            id = "pLastVisit",
            value = "join(address)->json_property(location, lastVisit) < ?")
        LocalDate lastVisit,
    @PredicateDef(id = "pAlias", value = "property(aliases) contains ?") String alias,
    @PredicateDef("join(address)->property(city) in ?") List<String> cities) {}
