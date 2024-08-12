import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.api.java.function.MapPartitionsFunction;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;

public class MapPartitionsHashColumnTest {

    private SparkSession spark;

    @Before
    public void setUp() {
        // Initialize SparkSession
        spark = SparkSession.builder()
                .appName("MapPartitionsHashColumnTest")
                .master("local")
                .getOrCreate();
    }

    @Test
    public void testMapPartitionsHashing() throws Exception {
        // Sample data
        List<Row> data = Arrays.asList(
                RowFactory.create(1, "value1", "data1", "data2", "data3"),
                RowFactory.create(2, "value2", "data4", "data5", "data6")
        );

        // Define schema
        List<StructField> fields = new ArrayList<>();
        fields.add(DataTypes.createStructField("id", DataTypes.IntegerType, false));
        fields.add(DataTypes.createStructField("xyz", DataTypes.StringType, false));
        fields.add(DataTypes.createStructField("col3", DataTypes.StringType, false));
        fields.add(DataTypes.createStructField("col4", DataTypes.StringType, false));
        fields.add(DataTypes.createStructField("col5", DataTypes.StringType, false));

        StructType schema = DataTypes.createStructType(fields);

        // Create DataFrame
        Dataset<Row> df = spark.createDataFrame(data, schema);

        // Mocking the MapPartitionsFunction
        MapPartitionsFunction<Row, Row> mapPartitionsFunction = Mockito.mock(MapPartitionsFunction.class);

        // Define the behavior of the mocked function
        Mockito.when(mapPartitionsFunction.call(Mockito.any(Iterator.class))).thenAnswer(invocation -> {
            Iterator<Row> partition = invocation.getArgument(0);
            List<Row> result = new ArrayList<>();
            while (partition.hasNext()) {
                Row row = partition.next();
                String hashedValue = "hashed_" + row.getAs("xyz"); // Mock hashing behavior
                List<Object> values = new ArrayList<>();
                for (String fieldName : row.schema().fieldNames()) {
                    if (fieldName.equals("xyz")) {
                        values.add(hashedValue);
                    } else {
                        values.add(row.getAs(fieldName));
                    }
                }
                result.add(RowFactory.create(values.toArray()));
            }
            return result.iterator();
        });

        // Apply the mocked mapPartitions function
        Dataset<Row> hashedDF = df.mapPartitions(mapPartitionsFunction, RowEncoder.apply(schema));

        List<Row> expectedData = Arrays.asList(
                RowFactory.create(1, "hashed_value1", "data1", "data2", "data3"),
                RowFactory.create(2, "hashed_value2", "data4", "data5", "data6")
        );

        // Collect results
        List<Row> actualData = hashedDF.collectAsList();

        // Validate the results
        assertEquals(expectedData, actualData);

        // Verify the behavior of the mock
        Mockito.verify(mapPartitionsFunction, Mockito.times(1)).call(Mockito.any(Iterator.class));
    }
}
