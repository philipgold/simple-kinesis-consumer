/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package simple.kinesis.consumer;


import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.ShutdownReason;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker.Builder;
import com.amazonaws.services.kinesis.model.Record;
import java.lang.invoke.MethodHandles;
import org.apache.commons.cli.*;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleConsumer implements IRecordProcessorFactory {
  public static final Logger LOGGER = LoggerFactory.getLogger(SimpleConsumer.class);

  private static final String DEFAULT_REGION_NAME =
      Regions.getCurrentRegion() == null ? "us-east-1" : Regions.getCurrentRegion().getName();

  private static final String DEFAULT_PROFILE_NAME = "default";

  private final String streamName;
  private final String streamRegion;
  private final String profile;

  public SimpleConsumer(String streamName, String streamRegion, String profile) {
    this.streamName = streamName;
    this.streamRegion = streamRegion;
    this.profile = profile;
  }

  private class RecordProcessor implements IRecordProcessor {

    @Override
    public void initialize(String shardId) {
      LOGGER.info("initialized shardId: " + shardId);
    }

    @Override
    public void processRecords(List<Record> records, IRecordProcessorCheckpointer checkpointer) {

      for (Record r : records) {
        // Get the timestamp of this run from the partition key.
        LOGGER.info("### PartitionKey: " + r.getPartitionKey());

        // Extract the sequence number. It's encoded as a decimal
        // string and placed at the beginning of the record data,
        // followed by a space. The rest of the record data is padding
        // that we will simply discard.
        try {
          byte[] b = new byte[r.getData().remaining()];
          r.getData().get(b);
          LOGGER.info(new String(b, "UTF-8"));
        } catch (Exception e) {
          LOGGER.error("Error parsing record" + e);
          System.exit(1);
        }
      }

      try {
        checkpointer.checkpoint();
      } catch (Exception e) {
        LOGGER.error("Error while trying to checkpoint during ProcessRecords" + e);
      }
    }

    @Override
    public void shutdown(IRecordProcessorCheckpointer checkpointer, ShutdownReason reason) {
      System.out.println("Shutting down, reason: " + reason);
      try {
        checkpointer.checkpoint();
      } catch (Exception e) {
        LOGGER.error("Error while trying to checkpoint during Shutdown" + e);
      }
    }
  }

  @Override
  public IRecordProcessor createProcessor() {
    return this.new RecordProcessor();
  }

  public static void main(String[] args) throws Exception {
    LOGGER.info("main");
    Options options = new Options()
        .addOption("streamRegion", false, "the region of the Kinesis stream")
        .addOption("streamName", true, "the name of the kinesis stream the events are sent to")
        .addOption("profile", false, "the profile name of the CredentialsProvider for conection to AWS KDS")
        .addOption("help", "print this help message");

    CommandLine line = new DefaultParser().parse(options, args);

    if (line.hasOption("help")) {
      new HelpFormatter().printHelp(MethodHandles.lookup().lookupClass().getName(), options);
      System.exit(1);
    } else if (!line.hasOption("streamName")) {
      throw new IllegalArgumentException("Missings 'streamName' argument");
    }

    SimpleConsumer consumer = new SimpleConsumer(
        line.getOptionValue("streamName"),
        line.getOptionValue("streamRegion", DEFAULT_REGION_NAME),
        line.getOptionValue("profile", DEFAULT_PROFILE_NAME)
        );

      consumer.consume();
  }

  private void consume(){
    KinesisClientLibConfiguration config =
        new KinesisClientLibConfiguration(
            "KinesisSimpleConsumer",
            this.streamName,
            new ProfileCredentialsProvider(this.profile),
            "KinesisSimpleConsumer")
            .withRegionName(streamRegion)
            .withInitialPositionInStream(InitialPositionInStream.TRIM_HORIZON);

    new Builder()
        .recordProcessorFactory(this)
        .config(config)
        .build()
        .run();

    LOGGER.info("Finished");

  }
}
