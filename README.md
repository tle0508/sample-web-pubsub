Sample - Pub-Sub
==========

![](kafka_architecture.jpg)



#####Setup Zookeeper
- Download the ZooKeeper 3.7.0 from [here](https://www.apache.org/dyn/closer.lua/zookeeper/zookeeper-3.7.0/apache-zookeeper-3.7.0-bin.tar.gz).    

- Unzip the file. Inside the `conf` directory, rename the file `zoo_sample.cfg` as `zoo.cfg`. 

- The `zoo.cfg` file keeps configuration for ZooKeeper, i.e. on which port the ZooKeeper instance will listen, data directory, etc.

- The default listen port is 2181. You can change this port by changing `clientPort`.

- The default data directory is `/tmp/data`. Change this to `../data`, as you will not want ZooKeeper's data to be deleted after some time. Create a folder with the name `data` in the ZooKeeper directory.

- Go to the `bin` directory.

- Start ZooKeeper by executing the command `zkServer`. or `zkServer.sh start` (on mac os).


#####Setup Kafka Server
- Download the latest stable version of Kafka from [here](https://www.apache.org/dyn/closer.cgi?path=/kafka/2.8.0/kafka_2.12-2.8.0.tgz).

- Unzip this file. The Kafka instance (Broker) configurations are kept in the config directory.

- Go to the `config` directory. Open the file `server.properties`.

- Remove the comment from listeners property, i.e. `listeners=PLAINTEXT://:9092`. The Kafka broker will listen on port 9092.

- Check the `zookeeper.connect` property and change it as per your needs. The Kafka broker will connect to this ZooKeeper instance.

- Go to the Kafka home directory and execute the command `.\bin\windows\kafka-server-start.bat .\config\server.properties` (on windows) or `./bin/kafka-server-start.sh config/server.properties` (on macos).

d
We will need to create a topic on kafka to store messages for bank transactions. Please execute the following command to create a topic named `sample`.

```
./kafka-topics.bat --create --topic sample --bootstrap-server localhost:9092
```


You can monitor the Kafka server to see if there is any incoming messages under this topic using the following command.

```
./kafka-console-consumer.bat --topic sample --from-beginning --bootstrap-server localhost:9092
```


Goto: http://localhost:8100/message/[some message] and try to send some message. After that, monitor if the consumer can receive the message.