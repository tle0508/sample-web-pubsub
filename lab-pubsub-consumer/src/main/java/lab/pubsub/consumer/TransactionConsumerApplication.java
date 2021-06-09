package lab.pubsub.consumer;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient

public class TransactionConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionConsumerApplication.class, args);
	}

}
