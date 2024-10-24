package com.smousseur.specification.api.example.service;

import com.smousseur.specification.api.example.model.request.SearchUserRequest;
import com.smousseur.specification.api.example.model.response.SearchUserResponse;
import com.smousseur.specification.api.example.repository.UserRepository;
import com.smousseur.specification.api.service.SpecificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final SpecificationService specificationService;

  public List<SearchUserResponse> search(SearchUserRequest request) {
    return userRepository.findAll(specificationService.generateSpecification(request)).stream()
        .map(user -> new SearchUserResponse(user.getId(), user.getName(), user.getEmail()))
        .toList();
  }
}
