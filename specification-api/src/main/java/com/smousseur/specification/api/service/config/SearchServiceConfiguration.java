package com.smousseur.specification.api.service.config;

import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

/** The type Search service configuration. */
public abstract class SearchServiceConfiguration {
  private final JdbcTemplate jdbcTemplate;
  private final String sqlDialect;

  protected SearchServiceConfiguration(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.sqlDialect =
        this.jdbcTemplate.execute(
            (ConnectionCallback<String>)
                connection -> connection.getMetaData().getDatabaseProductName());
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
}
