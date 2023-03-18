package password_manager.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database implements AutoCloseable {
    private Connection connection;

    public Database(String path) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + path.strip());
    }

    /**
     * Execute select queries
     */
    public ResultSet executeQuery(QueryBuilder qb) throws SQLException {
        return qb.prepareQuery(this.connection).executeQuery();
    }

    /**
     * Execute insert, delete, create, and update queries
     */
    public boolean executeAction(QueryBuilder qb) throws SQLException {
        return qb.prepareQuery(this.connection).execute();
    }

    @Override
    public void close() throws IOException, SQLException {
        if (connection != null)
            connection.close();
    }
}
