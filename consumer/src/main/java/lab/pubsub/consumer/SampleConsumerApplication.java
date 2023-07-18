package lab.pubsub.consumer;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient

public class SampleConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SampleConsumerApplication.class, args);
	}

}
