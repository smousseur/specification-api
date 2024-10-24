package com.smousseur.specification.api.example.controller;

import com.smousseur.specification.api.example.model.request.SearchUserRequest;
import com.smousseur.specification.api.example.model.response.SearchUserResponse;
import com.smousseur.specification.api.example.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  /**
   * Simple search request.
   *
   * @param searchRequest the search request
   * @return the list
   */
  @PostMapping("/search")
  public List<SearchUserResponse> search(@RequestBody SearchUserRequest searchRequest) {
    return userService.search(searchRequest);
  }
}
