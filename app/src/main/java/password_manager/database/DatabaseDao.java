package password_manager.database;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;

import password_manager.database.tables.Account;

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
        // accounts table
        this.db.executeAction(conn -> {
            return conn.prepareStatement(
                    "CREATE TABLE account (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, website_name TEXT NOT NULL, url TEXT, username BLOB NOT NULL, password BLOB, note BLOB) STRICT");
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

    private ResultSet queryAllAccounts() throws SQLException {
        return this.db.executeQuery(conn -> {
            var statement = conn.prepareStatement(
                    "SELECT * FROM account");

            return statement;
        });
    }

    public ArrayList<Account> getAccounts() throws SQLException {
        var resultSet = queryAllAccounts();
        var accounts = new ArrayList<Account>();

        while (resultSet.next()) {
            var id = resultSet.getLong("id");
            var websiteName = resultSet.getString("website_name");
            var username = resultSet.getBytes("username");

            String url;
            try {
                url = resultSet.getString("url");
            } catch (SQLException e) {
                url = "";
            }

            byte[] password;
            try {
                password = resultSet.getBytes("password");
            } catch (SQLException e) {
                password = new byte[0];
            }

            byte[] notes;
            try {
                notes = resultSet.getBytes("notes");
            } catch (SQLException e) {
                notes = new byte[0];
            }

            try {
                var acc = new Account(id, websiteName, url, new String(this.crypt.decryptString(username)),
                        new String(this.crypt.decryptString(password)), new String(this.crypt.decryptString(notes)));
                accounts.add(acc);
            } catch (BadPaddingException e) {
                throw new RuntimeException(e);
            }
        }

        return accounts;
    }

    /**
     * @return id of inserted account
     */
    public long addAccount(Account acc) throws SQLException {
        try {
            var encryptedUsername = this.crypt.encryptString(acc.getUsername());
            var encryptedPass = this.crypt.encryptString(acc.getPassword());
            var encryptedNotes = this.crypt.encryptString(acc.getNote());
            this.db.executeAction(conn -> {
                var statement = conn.prepareStatement(
                        "INSERT INTO account (website_name, url, username, password, note) VALUES (?, ?, ?, ?, ?)");
                statement.setString(1, acc.getWebsiteName());
                statement.setString(2, acc.getUrl());
                statement.setBytes(3, encryptedUsername);
                statement.setBytes(4, encryptedPass);
                statement.setBytes(5, encryptedNotes);

                return statement;
            });
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }

        // try {
        var result = this.db.executeQuery(conn -> {
            return conn.prepareStatement(
                    "SELECT seq FROM sqlite_sequence WHERE name = 'account' LIMIT 1");
        });
        return result.getLong("seq");
        // } catch (SQLException e) {
        // return 1;
        // }
    }

    public void updateAccount(Account acc) throws SQLException {
        try {
            var encryptedUsername = this.crypt.encryptString(acc.getUsername());
            var encryptedPass = this.crypt.encryptString(acc.getPassword());
            var encryptedNotes = this.crypt.encryptString(acc.getNote());
            this.db.executeAction(conn -> {
                var statement = conn.prepareStatement(
                        "UPDATE account SET website_name=?, url=?, username=?, password=?, note=? WHERE id=?");
                statement.setString(1, acc.getWebsiteName());
                statement.setString(2, acc.getUrl());
                statement.setBytes(3, encryptedUsername);
                statement.setBytes(4, encryptedPass);
                statement.setBytes(5, encryptedNotes);
                statement.setLong(6, acc.getId());

                return statement;
            });
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAccount(Account acc) throws SQLException {
        this.db.executeAction(conn -> {
            var statement = conn.prepareStatement(
                    "DELETE FROM account WHERE id=?");
            statement.setLong(1, acc.getId());

            return statement;
        });
    }
}
