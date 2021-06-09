package lab.pubsub.producer;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.kafka.core.KafkaTemplate;

import lab.pubsub.producer.Transaction.TransactionType;

public class TransactionGenerator implements Runnable {

	KafkaTemplate<String, Transaction> kafkaTemplate;

	@Override
	public void run() {
		boolean interrupted = false;
		while (!interrupted) {
			
			// generate a transaction randomly
			int transactionType = ThreadLocalRandom.current().nextInt(0, 2);
			int transactionAmount = ThreadLocalRandom.current().nextInt(50, 100001);
			Transaction transaction = new Transaction(TransactionType.values()[transactionType], transactionAmount);
			System.out.println(interrupted+" Transaction created "+ transactionType +" - "+transactionAmount);
			
			// TODO: push transaction to messaging broker
			kafkaTemplate.send("banktrans", transaction);

			try {
				// wait for 3 seconds before loop to generate a new transaction
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				System.out.println("Thread is interrupted");
				// set to true to break out of this loop
				interrupted = true;
			}
			

		}

	}

	public KafkaTemplate<String, Transaction> getKafkaTemplate() {
		return kafkaTemplate;
	}

	public void setKafkaTemplate(KafkaTemplate<String, Transaction> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

}
