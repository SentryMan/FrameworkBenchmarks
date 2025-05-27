package benchmark.repository;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import jakarta.inject.Named;

@Factory
public class EbeanDatabaseFactory {

  @Bean
  @Named("readOnly")
  Database readOnly() {
    var dataSourceConfig = new DataSourceConfig();
    dataSourceConfig.autoCommit(true);
    dataSourceConfig.readOnly(true);
    dataSourceConfig.username(null);
    DatabaseConfig config = new DatabaseConfig();
    config.readOnlyDatabase(true);
    config.setDataSourceConfig(dataSourceConfig);
    return DatabaseFactory.create(config);
  }

  @Bean
  @Named("updateOnly")
  Database updateOnly() {
    var dataSourceConfig = new DataSourceConfig();
    dataSourceConfig.autoCommit(false);
    DatabaseConfig config = new DatabaseConfig();
    config.setDataSourceConfig(dataSourceConfig);
    return DatabaseFactory.create(config);
  }
}
