package password_manager.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface QueryBuilder {
    public PreparedStatement prepareQuery(Connection conn) throws SQLException;
}
