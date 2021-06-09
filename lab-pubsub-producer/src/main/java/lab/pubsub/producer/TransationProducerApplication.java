package lab.pubsub.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TransationProducerApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(TransationProducerApplication.class, args);

	}

}
