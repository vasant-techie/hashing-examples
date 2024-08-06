import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

public class HashingExamples {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) {
        String input = "Hello, World!";
        try {
            System.out.println("MD5: " + hash("MD5", input));
            System.out.println("SHA-1: " + hash("SHA-1", input));
            System.out.println("SHA-256: " + hash("SHA-256", input));
            System.out.println("SHA-384: " + hash("SHA-384", input));
            System.out.println("SHA-512: " + hash("SHA-512", input));
            System.out.println("RIPEMD-160: " + hash("RIPEMD160", input));
            System.out.println("Whirlpool: " + hash("WHIRLPOOL", input));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String hash(String algorithm, String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm, "BC");
        byte[] hashBytes = digest.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
