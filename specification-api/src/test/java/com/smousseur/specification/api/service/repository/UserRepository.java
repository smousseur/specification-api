package com.smousseur.specification.api.service.repository;

import com.smousseur.specification.api.service.model.entity.TestSearchApiUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository
    extends JpaRepository<TestSearchApiUser, Long>, JpaSpecificationExecutor<TestSearchApiUser> {
  Optional<TestSearchApiUser> findByName(String nane);
}
