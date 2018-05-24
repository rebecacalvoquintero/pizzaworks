package database;

import config.DatabaseConfig;
import org.sql2o.Sql2o;
import org.sql2o.converters.UUIDConverter;
import org.sql2o.quirks.PostgresQuirks;

import java.util.UUID;

public class DatabaseSetUp {
    public static Sql2o sql2oFromDataBase() {
        String host = System.getenv("PGHOST");
        String port = System.getenv("PGPORT");
        String user = System.getenv("PGUSER");
        String databaseName = System.getenv("PGDATABASE");
        String password = System.getenv("PGPW");
        DatabaseConfig databaseConfig = new DatabaseConfig(host, port, user, databaseName, password);

        return new Sql2o("jdbc:postgresql://" + databaseConfig.getHost() + ":" + databaseConfig.getPort() + "/" + databaseConfig.getDatabaseName() + "",
                databaseConfig.getUser(), databaseConfig.getPassword(), new PostgresQuirks() {
            {
                // make sure we use default UUID converter.
                converters.put(UUID.class, new UUIDConverter());            }
        });
    }
}
