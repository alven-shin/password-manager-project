package password_manager;

import java.io.File;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    private Stage window;
    private Database db;

    @Override
    public void start(Stage stage) {
        this.window = stage;
        this.db = new Database();

        this.window.setTitle("Password Manager");
        this.showLogin();
        this.window.show();
    }

    private void showLogin() {
        File database = new File("data.db");
        if (database.exists()) {
            this.window.setScene(Login.loginScene(window, db));
        } else {
            this.window.setScene(Login.registerScene(window, db));
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    // void givenPassword_whenEncrypt_thenSuccess()
    // throws InvalidKeySpecException, NoSuchAlgorithmException,
    // IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
    // InvalidAlgorithmParameterException, NoSuchPaddingException {

    // String plainText = "www.baeldung.com";
    // String password = "baeldung";
    // String salt = "12345678";
    // IvParameterSpec ivParameterSpec = AESUtil.generateIv();
    // SecretKey key = AESUtil.getKeyFromPassword(password, salt);
    // String cipherText = AESUtil.encryptPasswordBased(plainText, key,
    // ivParameterSpec);
    // String decryptedCipherText = AESUtil.decryptPasswordBased(
    // cipherText, key, ivParameterSpec);
    // Assertions.assertEquals(plainText, decryptedCipherText);
    // }
}
