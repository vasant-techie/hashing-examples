import java.util.HashSet;
import java.util.Set;

public class HMacChecker {

    private static final Set<String> HMAC_ALGORITHMS = new HashSet<>();

    static {
        // Add known HMAC algorithms to the set
        HMAC_ALGORITHMS.add("HmacMD5");
        HMAC_ALGORITHMS.add("HmacSHA1");
        HMAC_ALGORITHMS.add("HmacSHA256");
        HMAC_ALGORITHMS.add("HmacSHA384");
        HMAC_ALGORITHMS.add("HmacSHA512");
        // Add more HMAC algorithms as needed
    }

    public static boolean isHMacAlgorithm(String algorithmName) {
        return HMAC_ALGORITHMS.contains(algorithmName);
    }

    public static void main(String[] args) {
        String[] testAlgorithms = {"HmacSHA256", "HmacMD5", "SHA-256", "HmacSHA3-256"};

        for (String algorithm : testAlgorithms) {
            System.out.println(algorithm + " is HMAC: " + isHMacAlgorithm(algorithm));
        }
    }
}
