package pizzeria.util;

import org.flywaydb.core.Flyway;
import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;

public class DatabaseInitializer {

  public static DataSource createDataSource() {
    PGSimpleDataSource ds = new PGSimpleDataSource();
    ds.setUrl(System.getenv().getOrDefault(
        "URPIZZA_DB_URL", "jdbc:postgresql://localhost:5432/pizzeria_db?TimeZone=Europe/Kyiv"));
    ds.setUser(System.getenv().getOrDefault("URPIZZA_DB_USER", "postgres"));
    ds.setPassword(System.getenv().getOrDefault("URPIZZA_DB_PASSWORD", "H27735311"));

    return ds;
  }

  public static void runMigrations(DataSource dataSource) {
    Flyway flyway = Flyway.configure()
        .dataSource(dataSource)
        .locations("classpath:db/migration")
        .baselineOnMigrate(false)
        .outOfOrder(true)
        .executeInTransaction(false)
        .load();

    var result = flyway.migrate();

    if (result.success) {
      System.out.printf(
          "Flyway: applied %d migrations, current version: %s%n",
          result.migrationsExecuted,
          result.targetSchemaVersion);
    }
  }

  public static DataSource initialize() {
    DataSource ds = createDataSource();
    runMigrations(ds);
    return ds;
  }
}
