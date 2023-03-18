package password_manager.database;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.sql.SQLException;

import javax.crypto.BadPaddingException;

public class DatabaseDao implements AutoCloseable {
    private Database db;
    private Encrypter crypt;

    /**
     * @throws IllegalArgumentException incorrect password
     */
    public DatabaseDao(String password) throws SQLException, IllegalArgumentException {
        this("data.db", password);
    }

    public DatabaseDao(String path, String password) throws SQLException, IllegalArgumentException {
        var dbFileExists = new File(path).exists();
        this.db = new Database(path);

        if (!dbFileExists) {
            this.initializeDb(password);
        } else {
            // validate password
            try {
                if (!validatePassword(password)) {
                    throw new IllegalArgumentException("Invalid password!");
                }
            } catch (BadPaddingException e) {
                throw new InvalidParameterException("Invalid password!");
            }
        }
    }

    private void initializeDb(String password) throws SQLException {
        var salt = Encrypter.generateSalt();
        this.crypt = new Encrypter(salt, password);

        // security table
        this.db.executeAction(conn -> {
            return conn.prepareStatement(
                    "CREATE TABLE security (encrypted_phrase BLOB NOT NULL, salt BLOB NOT NULL, unencrypted_phrase TEXT NOT NULL) STRICT");
        });
        // websites table
        this.db.executeAction(conn -> {
            return conn.prepareStatement(
                    "CREATE TABLE website (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, url TEXT) STRICT");
        });
        // accounts table
        this.db.executeAction(conn -> {
            return conn.prepareStatement(
                    "CREATE TABLE account (username BLOB NOT NULL, password BLOB, note BLOB, salt BLOB NOT NULL, website_id INTEGER NOT NULL, FOREIGN KEY(website_id) REFERENCES website(id)) STRICT");
        });

        // initialize security table
        var unencryptedString = Encrypter.generateRandomString();
        byte[] encryptedString;
        try {
            encryptedString = this.crypt.encryptString(unencryptedString);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }

        this.db.executeAction(conn -> {
            var statement = conn.prepareStatement(
                    "INSERT INTO security (encrypted_phrase, salt, unencrypted_phrase) VALUES (?, ?, ?)");
            statement.setBytes(1, encryptedString);
            statement.setBytes(2, salt);
            statement.setString(3, unencryptedString);

            return statement;
        });
    }

    private boolean validatePassword(String password) throws SQLException, BadPaddingException {
        var result = this.db.executeQuery(conn -> {
            return conn.prepareStatement("SELECT * FROM security LIMIT 1");
        });
        this.crypt = new Encrypter(result.getBytes("salt"), password);
        var decrypted_string = new String(this.crypt.decryptString(result.getBytes("encrypted_phrase")));
        return decrypted_string.equals(result.getString("unencrypted_phrase"));
    }

    @Override
    public void close() throws IOException, SQLException {
        this.db.close();
    }
}
