package com.smousseur.specification.api.example.model.request;

import com.smousseur.specification.api.annotation.SearchPath;
import com.smousseur.specification.api.annotation.SearchRequestObject;
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
@SearchRequestObject
public record SearchUserRequest(
    @SearchPath("property(name) like ?") String name,
    @SearchPath("join(address)->json_property(location, coord.x) = ?") Integer lattitude,
    @SearchPath("join(address)->json_property(location, street) like ?") String street,
    @SearchPath("property(lastConnection) >= ?") LocalDateTime lastConnection,
    @SearchPath("join(address)->json_property(location, lastVisit) < ?") LocalDate lastVisit,
    @SearchPath("property(aliases) contains ?") String alias,
    @SearchPath("join(address)->property(city) in ?") List<String> cities) {}
