import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

public class HashingWithEnum {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public enum HashAlgorithm {
        MD5("MD5"),
        SHA1("SHA-1"),
        SHA256("SHA-256"),
        SHA384("SHA-384"),
        SHA512("SHA-512"),
        RIPEMD160("RIPEMD160"),
        WHIRLPOOL("WHIRLPOOL");

        private final String algorithm;

        HashAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }

        public String getAlgorithm() {
            return algorithm;
        }

        public static boolean isAlgorithmAvailable(String algorithmName) {
            for (HashAlgorithm algo : HashAlgorithm.values()) {
                if (algo.getAlgorithm().equalsIgnoreCase(algorithmName)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static void main(String[] args) {
        String input = "Hello, World!";
        for (HashAlgorithm algo : HashAlgorithm.values()) {
            try {
                System.out.println(algo.name() + ": " + hash(algo, input));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        // Test if specific predefined algorithms are available
        System.out.println("Is SHA-256 available? " + HashAlgorithm.isAlgorithmAvailable("SHA-256"));
        System.out.println("Is SHA-3 available? " + HashAlgorithm.isAlgorithmAvailable("SHA-3")); // Not predefined
    }

    public static String hash(HashAlgorithm algorithm, String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm.getAlgorithm(), "BC");
        byte[] hashBytes = digest.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
