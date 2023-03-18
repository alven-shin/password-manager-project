package password_manager.database.tables;

public class Account {
    private long websiteID;
    private String username;
    private String password;
    private String note;
    private Byte[] salt;
}
