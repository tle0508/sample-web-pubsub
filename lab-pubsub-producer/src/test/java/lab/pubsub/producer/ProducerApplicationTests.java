package lab.pubsub.producer;

import java.util.Collections;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;


@SpringBootTest(properties = {"eureka.client.enabled:false"})
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class ProducerApplicationTests {

	private static final String TOPIC = "banktrans";
	
	@Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;
	
	 @Autowired
	 private TransactionProducerController controller;


	@Test
	public void testProducer() throws Exception {

		// start the generator
		controller.start();
		
		
		//Thread.sleep(10000);
		Consumer<Integer, String> consumer = configureConsumer();
		ConsumerRecord<Integer, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, TOPIC,4000);
		
		// test if message is not empty
	   Assertions.assertThat(singleRecord.value()).isNotEmpty();

		// stop the generator
		controller.stop();
	
	}

	private Consumer<Integer, String> configureConsumer() {
		Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
		consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		Consumer<Integer, String> consumer = new DefaultKafkaConsumerFactory<Integer, String>(consumerProps)
				.createConsumer();
		consumer.subscribe(Collections.singleton(TOPIC));
		return consumer;
	}

}
