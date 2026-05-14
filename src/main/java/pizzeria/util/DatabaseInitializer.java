package pizzeria.util;

import org.flywaydb.core.Flyway;
import javax.sql.DataSource;
import org.sqlite.SQLiteDataSource;

public class DatabaseInitializer {

  public static DataSource createDataSource() {
    SQLiteDataSource ds = new SQLiteDataSource();
    ds.setUrl("jdbc:sqlite:pizzeria.db");

    return ds;
  }

  public static void runMigrations(DataSource dataSource) {
    Flyway flyway = Flyway.configure()
        .dataSource(dataSource)
        .locations("classpath:db/migration", "classpath:db/seed")
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
