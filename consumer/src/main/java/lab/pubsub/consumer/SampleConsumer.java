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
	void listener(String transaction) {
		LOG.info(transaction);
		
		// process transaction here
	}
}
