package com.smousseur.specification.api;

import com.smousseur.specification.api.service.SpecificationService;
import com.smousseur.specification.api.service.SpecificationValidationService;
import com.smousseur.specification.api.service.config.SearchServiceConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class SearchApiConfiguration {
  @Bean
  public SpecificationService specificationService(SearchServiceConfiguration configuration) {
    return new SpecificationService(configuration);
  }

  @Bean
  public SpecificationValidationService specificationValidationService(
      SearchServiceConfiguration configuration, ResourceLoader resourceLoader) {
    return new SpecificationValidationService(configuration, resourceLoader);
  }
}
