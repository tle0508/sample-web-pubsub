package lab.pubsub.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleProducerController {
	
	//TODO: add the kafka template for configuration.
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	Thread generator;
	

	@GetMapping("/message/{msg}")
	public ResponseEntity<String> send(@PathVariable String msg) {
		
		kafkaTemplate.send("sample", msg);
		
		return new ResponseEntity<>("Message sent.", HttpStatus.OK);
	}
	@GetMapping("/greet/{name}")
	public ResponseEntity<String> greet(@PathVariable String name){
		kafkaTemplate.send("greet", name);
		return new ResponseEntity<>("Greeting "+name, HttpStatus.OK);
	}
 

}
