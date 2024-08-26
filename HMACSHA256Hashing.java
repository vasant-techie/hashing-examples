import org.apache.spark.api.java.function.MapPartitionsFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.Encoders;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Base64;

public class HMACSHA256Hashing {

    // Method to perform HMAC SHA256 hashing
    public static String hash(int value, String secret) throws Exception {
        Mac sha256HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256HMAC.init(secretKey);

        byte[] hashBytes = sha256HMAC.doFinal(Integer.toString(value).getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
            .appName("HMAC SHA256 Hashing Example")
            .getOrCreate();

        // Example data
        List<Row> data = new ArrayList<>();
        data.add(RowFactory.create(1, true));
        data.add(RowFactory.create(2, false));
        data.add(RowFactory.create(3, true));

        Dataset<Row> df = spark.createDataFrame(data, 
            Encoders.tuple(Encoders.INT(), Encoders.BOOLEAN()).schema());

        // Secret key for HMAC SHA256
        final String secret = "your-secret-key";

        // Apply mapPartitions to hash the 'x' column
        Dataset<Row> hashedDF = df.mapPartitions(new MapPartitionsFunction<Row, Row>() {
            @Override
            public Iterator<Row> call(Iterator<Row> input) throws Exception {
                List<Row> output = new ArrayList<>();
                while (input.hasNext()) {
                    Row row = input.next();
                    int x = row.getInt(0);
                    boolean y = row.getBoolean(1);
                    String hashedX = hash(x, secret);
                    output.add(RowFactory.create(hashedX, y));
                }
                return output.iterator();
            }
        }, RowEncoder.apply(df.schema()));

        hashedDF.show();
    }
}
