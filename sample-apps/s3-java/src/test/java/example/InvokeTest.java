package example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification.*;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.strategy.sampling.NoSamplingStrategy;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InvokeTest {

  public InvokeTest() {
    AWSXRayRecorderBuilder builder = AWSXRayRecorderBuilder.standard();
    builder.withSamplingStrategy(new NoSamplingStrategy());
    AWSXRay.setGlobalRecorder(builder.build());
  }

  @Test
  void invokeTest() throws IOException {
    AWSXRay.beginSegment("s3-java-test");
    String bucket = new String(Files.readAllLines(Paths.get("bucket-name.txt")).get(0));
    S3EventNotificationRecord record = new S3EventNotificationRecord("eu-west-1",
            "ObjectCreated:Put",
            "aws:s3",
            "2020-03-08T00:30:12.456Z",
            "2.1",
            new RequestParametersEntity(null),
            new ResponseElementsEntity(null, null),
            new S3Entity(null,
                    new S3BucketEntity(bucket,
                            new UserIdentityEntity(null),
                            "arn:aws:s3:::" + bucket),
                    new S3ObjectEntity("inbound/sample-s3-java.png",
                            new Long(0L),
                            null,
                            "",
                            null),
                    "1.0"),
            new UserIdentityEntity(null));
    ArrayList<S3EventNotificationRecord> records = new ArrayList<>();
    records.add(record);
    S3Event event = new S3Event(records);

    Context context = new TestContext();
    Handler handler = new Handler();
    String result = handler.handleRequest(event, context);
    assertTrue(result.contains("Ok"));
    AWSXRay.endSegment();
  }

}
