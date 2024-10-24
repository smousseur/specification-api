package com.smousseur.specification.api.service;

import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

/** The type Search service configuration. */
public abstract class SpecificationServiceConfiguration {
  private final String sqlDialect;

  private boolean doValidation = true;

  protected SpecificationServiceConfiguration(JdbcTemplate jdbcTemplate) {
    this.sqlDialect =
        jdbcTemplate.execute(
            (ConnectionCallback<String>)
                connection -> connection.getMetaData().getDatabaseProductName());
  }

  protected SpecificationServiceConfiguration(JdbcTemplate jdbcTemplate, boolean doValidation) {
    this(jdbcTemplate);
    this.doValidation = doValidation;
  }

  /**
   * Packages to scan string [ ].
   *
   * @return the string [ ]
   */
  public abstract String[] packagesToScan();

  /**
   * Gets sql dialect.
   *
   * @return the sql dialect
   */
  public String getSqlDialect() {
    return sqlDialect;
  }

  public boolean isDoValidation() {
    return doValidation;
  }
}
