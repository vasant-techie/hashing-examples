import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import java.util.Base64;

public class RSAExample {

    // Method to generate an RSA key pair
    public static KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048); // Key size (2048 bits for RSA)
        return generator.generateKeyPair();
    }

    // Method to encrypt data using the public key
    public static String encrypt(String plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes); // Convert to Base64 for easier reading
    }

    // Method to decrypt data using the private key
    public static String decrypt(String encryptedText, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes);
    }

    // Convert PublicKey to Base64 String
    public static String getPublicKeyAsString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    // Convert PrivateKey to Base64 String
    public static String getPrivateKeyAsString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    // Recreate PublicKey from Base64 String
    public static PublicKey getPublicKeyFromString(String key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);
    }

    // Recreate PrivateKey from Base64 String
    public static PrivateKey getPrivateKeyFromString(String key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePrivate(spec);
    }

    public static void main(String[] args) {
        try {
            // Generate RSA Key Pair (Public and Private Keys)
            KeyPair keyPair = generateRSAKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            // Convert keys to Base64 for easier representation
            String publicKeyStr = getPublicKeyAsString(publicKey);
            String privateKeyStr = getPrivateKeyAsString(privateKey);

            System.out.println("Public Key (Base64): " + publicKeyStr);
            System.out.println("Private Key (Base64): " + privateKeyStr);

            // Example plaintext
            String plainText = "Hello, RSA Encryption!";

            // Encrypt the message using the public key
            String encryptedMessage = encrypt(plainText, publicKey);
            System.out.println("Encrypted Message: " + encryptedMessage);

            // Decrypt the message using the private key
            String decryptedMessage = decrypt(encryptedMessage, privateKey);
            System.out.println("Decrypted Message: " + decryptedMessage);

            // Optionally: You can recreate keys from strings (as shown in case keys are transmitted/stored)
            PublicKey recreatedPublicKey = getPublicKeyFromString(publicKeyStr);
            PrivateKey recreatedPrivateKey = getPrivateKeyFromString(privateKeyStr);

            // Verify recreated keys by re-encrypting and decrypting
            String encryptedWithRecreatedKey = encrypt(plainText, recreatedPublicKey);
            String decryptedWithRecreatedKey = decrypt(encryptedWithRecreatedKey, recreatedPrivateKey);
            System.out.println("Decrypted with recreated keys: " + decryptedWithRecreatedKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
