package com.smousseur.specification.api.example;

import com.smousseur.specification.api.service.SpecificationService;
import com.smousseur.specification.api.service.SpecificationValidationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class ExampleApiApplication {
  public static void main(String[] args) {
    SpringApplication.run(ExampleApiApplication.class, args);
  }

  /**
   * Main bean to generate {@link org.springframework.data.jpa.domain.Specification}
   *
   * @param jdbcTemplate the jdbc template
   * @return the specification service
   */
  @Bean
  public SpecificationService specificationService(JdbcTemplate jdbcTemplate) {
    return new SpecificationService(jdbcTemplate);
  }

  /**
   * Optional bean to validate expression on classes annotated with {@link
   * com.smousseur.specification.api.annotation.SearchRequestObject}.
   *
   * @param resourceLoader the resource loader
   * @return the specification validation service
   */
  @Bean
  public SpecificationValidationService specificationValidationService(
      ResourceLoader resourceLoader) {
    return new SpecificationValidationService(resourceLoader) {
      @Override
      public String[] packagesToScan() {
        return new String[] {"com.smousseur.specification.api.example.model.request"};
      }
    };
  }
}
