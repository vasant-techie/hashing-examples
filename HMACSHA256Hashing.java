import org.apache.spark.api.java.function.MapPartitionsFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.*;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Base64;

public class DynamicSchemaConversion {

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
            .appName("Dynamic Schema Conversion Example")
            .getOrCreate();

        // Example data
        List<Row> data = new ArrayList<>();
        data.add(RowFactory.create(1, true));
        data.add(RowFactory.create(2, false));
        data.add(RowFactory.create(3, true));

        StructType schema = new StructType(new StructField[]{
            DataTypes.createStructField("x", DataTypes.IntegerType, false),
            DataTypes.createStructField("y", DataTypes.BooleanType, false)
        });

        Dataset<Row> df = spark.createDataFrame(data, schema);

        // Secret key for HMAC SHA256
        final String secret = "your-secret-key";

        // Dynamically modify the schema to change 'x' from Integer to String
        StructType newSchema = new StructType(
            Arrays.stream(schema.fields())
                .map(field -> {
                    if (field.name().equals("x") && field.dataType() == DataTypes.IntegerType) {
                        return DataTypes.createStructField("x", DataTypes.StringType, field.nullable());
                    } else {
                        return field;
                    }
                })
                .toArray(StructField[]::new)
        );

        // Apply mapPartitions with dynamic schema conversion
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
        }, RowEncoder.apply(newSchema)); // Use the dynamically created schema

        hashedDF.show();
    }
}
