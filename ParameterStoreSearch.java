import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;
import software.amazon.awssdk.services.ssm.model.DescribeParametersRequest;
import software.amazon.awssdk.services.ssm.model.DescribeParametersResponse;
import software.amazon.awssdk.services.ssm.model.ParameterMetadata;

import java.util.List;

public class ParameterStoreSearch {

    public static void main(String[] args) {
        // Define the AWS region
        Region region = Region.US_EAST_1;
        
        // Initialize the SSM client
        try (SsmClient ssmClient = SsmClient.builder().region(region).build()) {

            // Search for parameters containing a specific string in their value
            String searchTerm = "example-search-term";
            searchParametersByContent(ssmClient, searchTerm);
        }
    }

    private static void searchParametersByContent(SsmClient ssmClient, String searchTerm) {
        // Fetch all parameters
        String nextToken = null;

        do {
            DescribeParametersRequest describeParametersRequest = DescribeParametersRequest.builder()
                    .nextToken(nextToken)
                    .build();

            DescribeParametersResponse describeResponse = ssmClient.describeParameters(describeParametersRequest);
            List<ParameterMetadata> parameterMetadataList = describeResponse.parameters();
            nextToken = describeResponse.nextToken();

            // Process each parameter
            for (ParameterMetadata metadata : parameterMetadataList) {
                String paramName = metadata.name();

                // Get the parameter value
                GetParameterRequest getParameterRequest = GetParameterRequest.builder()
                        .name(paramName)
                        .withDecryption(true)
                        .build();

                GetParameterResponse getParameterResponse = ssmClient.getParameter(getParameterRequest);
                Parameter parameter = getParameterResponse.parameter();
                String paramValue = parameter.value();

                // Check if the parameter's value contains the search term
                if (paramValue.contains(searchTerm)) {
                    System.out.println("Found parameter: " + paramName);
                    System.out.println("Parameter value: " + paramValue);
                }
            }

        } while (nextToken != null);
    }
}
