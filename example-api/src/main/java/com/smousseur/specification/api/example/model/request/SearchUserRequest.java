package com.smousseur.specification.api.example.model.request;

import com.smousseur.specification.api.annotation.SearchPath;
import com.smousseur.specification.api.annotation.SearchRequestObject;

/**
 * Tag this class as a entry point to generate JPA specifications.
 *
 * @param name Perform a search using User.name like '%name%'
 * @param lattitude Perform a search using User.address.location json field and $.coord.x =
 *     lattitude
 * @param street Perform a search using User.address.location json field and $.street like
 *     '%street%'
 */
@SearchRequestObject
public record SearchUserRequest(
    @SearchPath("path(name) like ?") String name,
    @SearchPath("join(address)->json_path(location, coord.x, int) = ?") Integer lattitude,
    @SearchPath("join(address)->json_path(location, street) like ?") String street) {}
