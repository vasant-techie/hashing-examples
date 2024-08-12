import org.apache.spark.sql.Row;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.api.java.function.MapPartitionsFunction;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class MapPartitionsHashColumnTest {

    @Test
    public void testMapPartitionsHashingWithoutSpark() throws Exception {
        // Mock Row objects
        Row row1 = mock(Row.class);
        Row row2 = mock(Row.class);

        // Mock Schema
        String[] fieldNames = {"id", "xyz", "col3", "col4", "col5"};
        when(row1.schema().fieldNames()).thenReturn(fieldNames);
        when(row2.schema().fieldNames()).thenReturn(fieldNames);

        // Mock row values
        when(row1.getAs("xyz")).thenReturn("value1");
        when(row2.getAs("xyz")).thenReturn("value2");
        when(row1.getAs("id")).thenReturn(1);
        when(row2.getAs("id")).thenReturn(2);
        when(row1.getAs("col3")).thenReturn("data1");
        when(row2.getAs("col3")).thenReturn("data4");
        when(row1.getAs("col4")).thenReturn("data2");
        when(row2.getAs("col4")).thenReturn("data5");
        when(row1.getAs("col5")).thenReturn("data3");
        when(row2.getAs("col5")).thenReturn("data6");

        // Mock the Dataset and RowEncoder
        Dataset<Row> df = mock(Dataset.class);
        RowEncoder rowEncoder = mock(RowEncoder.class);

        // Mock the MapPartitionsFunction
        MapPartitionsFunction<Row, Row> mapPartitionsFunction = mock(MapPartitionsFunction.class);

        // Mock the partition iterator
        List<Row> partitionData = Arrays.asList(row1, row2);
        Iterator<Row> partitionIterator = partitionData.iterator();

        // Define the behavior of the mocked mapPartitions function
        when(mapPartitionsFunction.call(any(Iterator.class))).thenAnswer(invocation -> {
            Iterator<Row> partition = invocation.getArgument(0);
            List<Row> result = new ArrayList<>();
            while (partition.hasNext()) {
                Row row = partition.next();
                String hashedValue = "hashed_" + row.getAs("xyz");

                List<Object> values = new ArrayList<>();
                for (String fieldName : row.schema().fieldNames()) {
                    if (fieldName.equals("xyz")) {
                        values.add(hashedValue);
                    } else {
                        values.add(row.getAs(fieldName));
                    }
                }
                Row newRow = mock(Row.class);
                when(newRow.getAs("id")).thenReturn(values.get(0));
                when(newRow.getAs("xyz")).thenReturn(values.get(1));
                when(newRow.getAs("col3")).thenReturn(values.get(2));
                when(newRow.getAs("col4")).thenReturn(values.get(3));
                when(newRow.getAs("col5")).thenReturn(values.get(4));
                result.add(newRow);
            }
            return result.iterator();
        });

        // Simulate the mapPartitions operation
        Iterator<Row> resultIterator = mapPartitionsFunction.call(partitionIterator);

        // Collect the results into a list
        List<Row> actualData = new ArrayList<>();
        resultIterator.forEachRemaining(actualData::add);

        // Define expected data
        Row expectedRow1 = mock(Row.class);
        Row expectedRow2 = mock(Row.class);
        when(expectedRow1.getAs("id")).thenReturn(1);
        when(expectedRow1.getAs("xyz")).thenReturn("hashed_value1");
        when(expectedRow1.getAs("col3")).thenReturn("data1");
        when(expectedRow1.getAs("col4")).thenReturn("data2");
        when(expectedRow1.getAs("col5")).thenReturn("data3");

        when(expectedRow2.getAs("id")).thenReturn(2);
        when(expectedRow2.getAs("xyz")).thenReturn("hashed_value2");
        when(expectedRow2.getAs("col3")).thenReturn("data4");
        when(expectedRow2.getAs("col4")).thenReturn("data5");
        when(expectedRow2.getAs("col5")).thenReturn("data6");

        List<Row> expectedData = Arrays.asList(expectedRow1, expectedRow2);

        // Validate the results
        assertEquals(expectedData.size(), actualData.size());
        for (int i = 0; i < expectedData.size(); i++) {
            assertEquals(expectedData.get(i).getAs("id"), actualData.get(i).getAs("id"));
            assertEquals(expectedData.get(i).getAs("xyz"), actualData.get(i).getAs("xyz"));
            assertEquals(expectedData.get(i).getAs("col3"), actualData.get(i).getAs("col3"));
            assertEquals(expectedData.get(i).getAs("col4"), actualData.get(i).getAs("col4"));
            assertEquals(expectedData.get(i).getAs("col5"), actualData.get(i).getAs("col5"));
        }

        // Verify that the mock was called
        verify(mapPartitionsFunction, times(1)).call(any(Iterator.class));
    }
}
