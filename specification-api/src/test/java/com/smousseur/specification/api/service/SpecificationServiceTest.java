package com.smousseur.specification.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.smousseur.specification.api.TestSearchApiConfiguration;
import com.smousseur.specification.api.service.model.TestSearchApiModel;
import com.smousseur.specification.api.service.model.entity.TestSearchApiUser;
import com.smousseur.specification.api.service.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

@SpringJUnitConfig
@ContextConfiguration(
    classes = {TestSearchApiConfiguration.class},
    loader = AnnotationConfigContextLoader.class)
@Sql(scripts = {"/sql/schema.sql", "/sql/data.sql"})
@Transactional
class SpecificationServiceTest {
  @Autowired private SpecificationService specificationService;
  @Autowired private UserRepository userRepository;

  @Test
  void testFindLikeName() {
    TestSearchApiModel searchModel = new TestSearchApiModel();
    searchModel.setName("adm");
    Specification<TestSearchApiUser> specification =
        specificationService.generateSpecification(searchModel);
    List<TestSearchApiUser> users = userRepository.findAll(specification);
    TestSearchApiUser admin = userRepository.findByName("admin").get();
    assertEquals(1, users.size());
    assertEquals(admin.getId(), users.get(0).getId());
    searchModel.setName("admz");
    specification = specificationService.generateSpecification(searchModel);
    users = userRepository.findAll(specification);
    assertTrue(users.isEmpty());
  }

  @Test
  void testFindEqual() {
    TestSearchApiModel searchModel = new TestSearchApiModel();
    searchModel.setCountry("france");
    searchModel.setActive(true);
    Specification<TestSearchApiUser> specification =
        specificationService.generateSpecification(searchModel);
    List<TestSearchApiUser> users = userRepository.findAll(specification);
    TestSearchApiUser admin = userRepository.findByName("admin").get();
    assertEquals(1, users.size());
    assertEquals(admin.getId(), users.get(0).getId());
    searchModel.setActive(false);
    specification = specificationService.generateSpecification(searchModel);
    users = userRepository.findAll(specification);
    assertTrue(users.isEmpty());
  }

  @Test
  void testFindWithJoin() {
    TestSearchApiModel searchModel = new TestSearchApiModel();
    searchModel.setLatitude(100L);
    searchModel.setAltitude(30.56f);
    Specification<TestSearchApiUser> specification =
        specificationService.generateSpecification(searchModel);
    List<TestSearchApiUser> users = userRepository.findAll(specification);
    TestSearchApiUser admin = userRepository.findByName("admin").get();
    assertEquals(1, users.size());
    assertEquals(admin.getId(), users.get(0).getId());
    searchModel.setAltitude(30.57f);
    specification = specificationService.generateSpecification(searchModel);
    users = userRepository.findAll(specification);
    assertTrue(users.isEmpty());
  }

  @Test
  void testFindWithJoinTypes() {
    TestSearchApiModel searchModel = new TestSearchApiModel();
    searchModel.setAddressId(1);
    searchModel.setStreet("test street");
    searchModel.setTemperature(28.95d);
    Specification<TestSearchApiUser> specification =
        specificationService.generateSpecification(searchModel);
    List<TestSearchApiUser> users = userRepository.findAll(specification);
    TestSearchApiUser admin = userRepository.findByName("admin").get();
    assertEquals(1, users.size());
    assertEquals(admin.getId(), users.get(0).getId());
    searchModel.setTemperature(28.96d);
    specification = specificationService.generateSpecification(searchModel);
    users = userRepository.findAll(specification);
    assertTrue(users.isEmpty());
  }
}
