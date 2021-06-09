package lab.pubsub.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionProducerController {
	
	//TODO: add the kafka template for configuration.
	@Autowired
	private KafkaTemplate<String, Transaction> kafkaTemplate;

	Thread generator;
	

	@GetMapping("/producer/start")
	public ResponseEntity<String> start() {
		
		TransactionGenerator genTask = new TransactionGenerator();
		
		genTask.setKafkaTemplate(kafkaTemplate);
		
		generator = new Thread(genTask);
		generator.start();
		return new ResponseEntity<>("Producer started.", HttpStatus.OK);
	}

	@GetMapping("/producer/stop")
	public ResponseEntity<String> stop() {
		generator.interrupt();
		try {
			generator.join();
		} catch (InterruptedException e) {

			e.printStackTrace();
			new ResponseEntity<>("Error occured -> " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>("Producer stopped.", HttpStatus.OK);

	}

}
