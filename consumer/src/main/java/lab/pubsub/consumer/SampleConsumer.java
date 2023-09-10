package lab.pubsub.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SampleConsumer {
	Logger LOG = LoggerFactory.getLogger(SampleConsumer.class);

	//TODO: add listener methods here
	@KafkaListener(topics = "sample")
	void listen(String transaction) {
		LOG.info(transaction);
		
		// process transaction here
	}
	@KafkaListener(topics = "greet")
	void listenGreet(String name) {
		LOG.info("hello"+name.toUpperCase());
		
		// process transaction here
	}
}
