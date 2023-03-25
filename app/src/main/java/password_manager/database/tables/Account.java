package password_manager.database.tables;

import java.sql.SQLException;

import password_manager.database.DatabaseDao;

public class Account {
    private long id;
    private String websiteName;
    private String url;
    private String username;
    private String password;
    private String note;

    public Account(long id, String websiteName, String url, String username, String password, String note) {
        this.id = id;
        this.websiteName = websiteName;
        this.url = url;
        this.username = username;
        this.password = password;
        this.note = note;
    }

    public Account(String websiteName, String url, String username, String password, String note, DatabaseDao dao)
            throws SQLException {
        this.websiteName = websiteName;
        this.url = url;
        this.username = username;
        this.password = password;
        this.note = note;
        this.id = dao.addAccount(this);
    }

    @Override
    public String toString() {
        return this.websiteName + '\n' + this.username;
    }

    public String getWebsiteName() {
        return websiteName;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNote() {
        return note;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getId() {
        return id;
    }

}
