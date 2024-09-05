import org.bouncycastle.util.encoders.Hex;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncodingExamples {

    // Base64 Encoding
    public static String base64Encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    // URL Encoding
    public static String urlEncode(String data) throws UnsupportedEncodingException {
        return URLEncoder.encode(data, StandardCharsets.UTF_8.name());
    }

    // ASCII Encoding
    public static byte[] asciiEncode(String data) {
        return data.getBytes(StandardCharsets.US_ASCII);
    }

    // UTF-8 Encoding
    public static byte[] utf8Encode(String data) {
        return data.getBytes(StandardCharsets.UTF_8);
    }

    // UTF-16 Encoding
    public static byte[] utf16Encode(String data) {
        return data.getBytes(StandardCharsets.UTF_16);
    }

    // Hexadecimal Encoding
    public static String hexEncode(byte[] data) {
        return new String(Hex.encode(data));
    }

    // Binary Encoding
    public static String binaryEncode(byte[] data) {
        StringBuilder binaryString = new StringBuilder();
        for (byte b : data) {
            binaryString.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
        return binaryString.toString();
    }

    // EBCDIC Encoding (using IBM’s libraries or custom implementation is required for full support)
    public static byte[] ebcdicEncode(String data) throws UnsupportedEncodingException {
        return data.getBytes("Cp1047"); // EBCDIC encoding for example
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String sampleText = "Hello, World!";

        System.out.println("Base64: " + base64Encode(sampleText.getBytes()));
        System.out.println("URL: " + urlEncode(sampleText));
        System.out.println("ASCII: " + new String(asciiEncode(sampleText), StandardCharsets.US_ASCII));
        System.out.println("UTF-8: " + new String(utf8Encode(sampleText), StandardCharsets.UTF_8));
        System.out.println("UTF-16: " + new String(utf16Encode(sampleText), StandardCharsets.UTF_16));
        System.out.println("Hex: " + hexEncode(sampleText.getBytes()));
        System.out.println("Binary: " + binaryEncode(sampleText.getBytes()));
        System.out.println("EBCDIC: " + new String(ebcdicEncode(sampleText), Charset.forName("Cp1047")));
    }
}
