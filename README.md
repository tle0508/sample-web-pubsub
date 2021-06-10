Lab - Pub-Sub
==========

Before you start
----------
The purpose of this lab is to explore the concept of publish-subscribe pattern using messaging system. We will use Apache Kafka as messagning system, which is suitable to process big data. 

Begin by cloning your repository onto your local machine. Import it into eclipse. This project includes three projects `lab-pubsub-consumer`, `lab-pubsub-eureka-naming-server` and `lab-pubsub-producer`. Please import them as maven project in eclipse by Import > Maven project and choose pom file in these projects. 

Banking System
----------
In this lab, you will develop a part of banking system. It have microservices that process the transactions of banking such as depositing and withdrawal from the mobile and web banking application. There are two services namely: `consumer` and `producer`. The `producer` randomly generate the transactions, which mimic the incoming transaction from mobile and web banking application. The `consumer` helps to process the transaction by simply printing them out (in the real world, the consumer would invoke the core banking to process the transaction). 

We can no longer rely on service-to-service call like we have done in the microservice lab. As there could be millions of transaction per minute, we need to make sure that the consumer is robust and scalable enough to support such processing so we may have multiple consumers. When all consumer is down, the producer should still be working. We apply Apache Kafka to achieve this scalable pub-sub processing as shown in the figure below.

![](overview.png)


Apache kafka's architecture can be seen below. The producers create messages to send, while the consumers receive the message to process. These messages is used to communicated between software component so they can be data to process, or events triggered by some components in the software system to let the other components to make further processing.  The kafka cluster consists of kafka brokers (server) that store messages. Zookeeper keeps track of broker joining or leaving the cluster. Zookeeper also helps to make sure that topics are synchonised across all brokers in the cluster.

![](kafka_architecture.jpg)

Exercise 1 - Producer
----------
First, we need to set up Apacha kafka. 

#### a) Setting up
This step involve settingup Zookeeper and Apache Kafka on your machine. In this exercise, we will setup 1 zookeeper and 1 instance of Kafka broker

#####Setup Zookeeper
- Download the ZooKeeper 3.7.0 from [here](https://www.apache.org/dyn/closer.lua/zookeeper/zookeeper-3.7.0/apache-zookeeper-3.7.0-bin.tar.gz).    

- Unzip the file. Inside the `conf` directory, rename the file `zoo_sample.cfg` as `zoo.cfg`. 

- The `zoo.cfg` file keeps configuration for ZooKeeper, i.e. on which port the ZooKeeper instance will listen, data directory, etc.

- The default listen port is 2181. You can change this port by changing `clientPort`.

- The default data directory is `/tmp/data`. Change this to `../data`, as you will not want ZooKeeper's data to be deleted after some time. Create a folder with the name `data` in the ZooKeeper directory.

- Go to the `bin` directory.

- Start ZooKeeper by executing the command `./zkServer.sh start`.

- Stop ZooKeeper by stopping the command `./zkServer.sh stop`.

#####Setup Kafka Server
- Download the latest stable version of Kafka from [here](https://www.apache.org/dyn/closer.cgi?path=/kafka/2.8.0/kafka_2.12-2.8.0.tgz).

- Unzip this file. The Kafka instance (Broker) configurations are kept in the config directory.

- Go to the `config` directory. Open the file `server.properties`.

- Remove the comment from listeners property, i.e. `listeners=PLAINTEXT://:9092`. The Kafka broker will listen on port 9092.

- Check the `zookeeper.connect` property and change it as per your needs. The Kafka broker will connect to this ZooKeeper instance.

- Go to the Kafka home directory and execute the command `./bin/kafka-server-start.sh config/server.properties`.

- Stop the Kafka broker through the command `./bin/kafka-server-stop.sh`.

We will need to create a topic on kafka to store messages for bank transactions. Please execute the following command to create a topic named `banktrans`.

```
./kafka-topics.sh --create --topic banktrans --bootstrap-server localhost:9092
```

You can monitor the Kafka server to see if there is any incoming messages under this topic using the following command.

```
./kafka-console-consumer.sh --topic banktrans --from-beginning --bootstrap-server localhost:9092
```

#### b) Developing Producer
We will develop a producer that randomly generate a bank transaction every 3 seconds. This producer resided in `lab-pubsub-producer` is developed using Spring Boot and dependency to Kafka's library. Please make sure the following dependencies included in `pom.xml`

```
		<dependency>
			<groupId>org.springframework.kafka</groupId>
			<artifactId>spring-kafka</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.kafka</groupId>
			<artifactId>spring-kafka-test</artifactId>
			<scope>test</scope>
		</dependency>
```

 There are the following classes:

- `TransactionProducerApplication` is simply a spring boot application

- `Transaction` is a class that represents the bank transaction. It consists of transaction type: `Withdraw` and `Deposit` as well as the amount of money. 

- `TransactionConfig` contains the configuration that are required to connec to kafka server, please add the lines below in `producerFactory()`. The first line specifis where the kafka server is running. The second line specifies how to serialise key to store on the Kafka server. The third line specifies that the value (object of `Transaction`) should be serialised in JSON to store on Kafka.   

```
config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
```

- `TransactionProducerController` is a rest controller consisting of two methods, `start()` for starting the generator (`TransactionGenerator`) that randomly generates the transactions and `stop()` for stopping it. Then, we need to automatically wire the kafka configuration by adding the following line to the class. The KafkaTemplate will be initialised with Kafka's configuration as string and model class object.

```
@Autowired
private KafkaTemplate<String, Transaction> kafkaTemplate;
```


- `TransactionGenerator` implements a runnable class that generates the transaction randomly and push the message to the kafka server. Please add the  line below after we randomly generate the transaction. This statement sends `transation` under `banktrans` topic. This kafkaTemplate is automatically wired in `TransactionController`, you need to find a way to pass that to this class.

```
kafkaTemplate.send("banktrans", transaction);
```

Now you can start this application (pleases make sure you start Eureka naming server before) and invoke GET request to `http://localhost:8100/producer/start` to start the generator and `http://localhost:8100/producer/stop` to stop the generator. 

When you start the generator, the random transactions should be created and sent to the kafka server. The consumer console (`kafka-console-consumer.sh` that you started in setting up step) should list those transactions as sample shown below.

```
{"type":"Withdraw","amount":21954}
{"type":"Deposit","amount":99120}
{"type":"Deposit","amount":65775}
{"type":"Withdraw","amount":96887}
...
```


#### c) Unit Testing Kafka
The test `ProducerApplicationTests` helps to automatically check if the producer works properly. We have below line to disable the discovery client as we only want to test if the producer works well with the Kafka. 

```
@SpringBootTest(properties = {"eureka.client.enabled:false"})
```

This test uses mocked kafka so we need to specified `@EmbeddedKafka` annotation to setting up the kafka server at the specified port (9092).

```
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
```

 After the generator is started, the mocked consumer `Consumer` is created using `configureConsumer()` method. The `KafkaTestUtils.getSingleRecord()` fetches the latest record published to Kafka with time out at 4000 milliseconds.

The `Assertions.assertThat(singleRecord.value()).isNotEmpty();` checks if the messages published by the generator is not empty.

Please try to run this test by running this test class or use maven's `verify` goal.


#### d) Questions
In TransactionProducerController, the  `start`, `interrupt` and `join` method are called. What are these methods for Java Thread class for?

Please answer this question in your journal and here.

```
Your thoughts here
```



Exercise 2 - Consumer
----------
You can find the consumer in `lab-pubsub-consumer`. This is a simple Spring boot application. It consists of three classes as follows:

- `TransactionConfig` contains the configuration to connect to the Kafka server and how to deserialise the message.
- `TransactionConsumer` is a Spring component that listen to topic `banktrans` and print out the message as a log. You must add the following method to listen to the topic.

```
@KafkaListener(topics = "banktrans")
	void listener(String transaction) {
		LOG.info(transaction);
	}
```

You can run the consumer from `TransactionConsumerApplication`. The consumer will wait for the incoming messages sent by the producer and print out the messages as sample below.

```
{"type":"Withdraw","amount":93034}
{"type":"Deposit","amount":42685}
{"type":"Deposit","amount":2874}
```

To make the consumer more reliable, we add more instances of consumer by running `TransactionConsumerApplication` on different port (add vm argument `-Dserver.port=8001`). The messages should print out from the console of all instances of consumer. 

Please  modify the source code so that the consumer only listens to `Withdraw` transaction and explain your modification below. 
*Hint: Please see this [link](https://www.baeldung.com/spring-kafka) for how to implment the message filters.*

```
Your modification
```

#### Reflection
How can pub-sub handle a large volume of transactions? 

```
Your though here
```


 