# 日志收集系统简易搭建
目标：logstash+elasticsearch+kibana分布式集群日志收集系统



### 组件版本

| 名称          | 版本       |
| ------------- | ---------- |
| kibana        | 7.9.1      |
| elasticsearch | 7.9.1      |
| kafka         | 2.11-2.1.0 |
| logstash      | 7.9.1      |



### 启动kafka，建立topic

#### Unix
以下命令执行需根据具体文件路径进行调整
- cd kafka解压后的目录内

- 运行 zookeeper  

  bin/zookeeper-server-start.sh config/zookeeper.properties

- 运行 broker(kafka server)   

  bin/kafka-server-start.sh config/server.properties

- 创建topic

  bin/kafka-topics.sh --create --zookeeper localhost:2181 --topic 你的topic名称 --partirion 1 --replication-factor 1

- 检查topic

  bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic 你的topic名称



#### windows

- cd kafka解压后的目录内

- 运行 zookeeper  
  bin/windows/zookeeper-server-start.bat config/zookeeper.properties
- 运行 broker(kafka server)   
  bin/windows/kafka-server-start.bat config/server.properties
- 创建 topic   
  bin/windows/kafka-topics.bat --create --zookeeper localhost:2181 --topic 你的topic名称 --partitions 1 --replication-factor 1
- 检查 topic  
  bin/windows/kafka-topics.bat --describe --zookeeper localhost:2181 --topic 你的topic名称



### 配置log4j2

- 配置maven依赖

  ```xml
  <dependency>    
      <groupId>org.springframework.boot</groupId>    
      <artifactId>spring-boot-starter</artifactId>    
      <!-- 去除springboot自带的日志 -->       
      <exclusions>        
      	<exclusion>            
      	<groupId>org.springframework.boot</groupId>            
      	<artifactId>spring-boot-starter-logging</artifactId>        
      	</exclusion>    
    	</exclusions>
  </dependency>
  
  <dependency>    
  	<groupId>org.springframework.boot</groupId>   
      <artifactId>spring-boot-starter-log4j2</artifactId>
  </dependency>
  ```


- 配置log4j2

  - application.yml

    ```
    server.port=8800
    logging.config=classpath:log4j2-kafka.xml
    ```

  - 新建log4j2-kafka.xml

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <!--日志级别以及优先级排序: OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE > ALL -->
    <Configuration status="OFF">
        <Appenders>
            <!-- 输出控制台日志的配置 -->
            <Console name="console" target="SYSTEM_OUT">
                <ThresholdFilter level="DEBUG" onMatch="ACCEPT" onMismatch="DENY"/>
                <!-- 输出日志的格式 -->
                <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss SSS} [%t] %-5level %logger{36} - %msg%n"/>
            </Console>
            <Kafka name="Kafka" topic="指定topic名称">
                 <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS}[%t] %-5level [%l] - %msg"/>
                 <Property name="bootstrap.servers">你的kafka服务器地址:9092</Property>
            </Kafka>
        </Appenders>
    
        <Loggers>
            <Root level="ALL">
                <AppenderRef ref="Kafka"/>
                <AppenderRef ref="console"/>
            </Root>
            <Logger name="org.apache.kafka" level="INFO"/>
            <logger name="org.springframework" level="INFO"/>
        </Loggers>
    </Configuration>
    ```

- 服务器执行下面的命令消费指定kafka topic

  ```shell
  # linux
  bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic topic名称  --from-beginning
  # windows
  bin/windows/kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic topic名称  --from-beginning
  ```

- 启动项目，此时应该可以看到kafka已经打印出来了启动日志！



### 安装 logstash，elasticsearch，kibana

- 具体安装方式可参考[官方文档](https://www.elastic.co/guide/index.html)，也可查阅网上资料

- 根据部署机器的实际情况，适当调整组件的 JVM 内存

- logstash 配置文件

  ```
  input {
      kafka {
          bootstrap_servers => ["你的kafka服务器地址:9092"]
          topics => ["applogs"]
          type => "log4j2-kafka"
      }
  }
  output {
      stdout {
        codec => rubydebug
      }
      elasticsearch {
          hosts => ["你的elasticsearch服务器地址:9200"]
          index => "app-logstash-%{+YYYY.MM.dd.HH}"
      }
  }
  ```



### 待做

- 加上过滤器，将日志数据格式化
- 配置 nginx 日志处理，file 读取方式
- 集群化，zk，kafka
- 集成到具体项目，尽量无侵入



### 更新

Grok 支持把预定义的 *grok 表达式* 写入到文件中，官方提供的预定义 grok 表达式见：<https://github.com/logstash/logstash/tree/v1.4.2/patterns>。建议是把所有的 grok 表达式统一写入到一个地方。然后用 filter/grok 的 patterns_dir 选项来指明。

* 自定义 grok 表达式文件

```
CURR_THREAD ([a-zA-Z]+?\-[a-zA-Z]+?\-[0-9]+?\-[a-zA-Z]+?\-[0-9])
INVOKE_METHOD (.+?\(.+?\))
LOG_MSG (.*)
```



- logstash 配置文件

  ```
  input {
      kafka {
          bootstrap_servers => ["你的kafka服务器地址:9092"]
          topics => ["app-logs"]
          type => "kafka-log"
      }
  }
  filter {
      grok {
          patterns_dir => ["patterns存放目录路径"]
          match => {
              "message" => "%{TIMESTAMP_ISO8601:timestamp}\[%{CURR_THREAD:threadname}\]\s%{LOGLEVEL:loglevel}\s\s\[%{INVOKE_METHOD:method}\]\s-\s%{LOG_MSG:information}"
          }
      }
  }
  output {
      stdout {
        codec => rubydebug
      }
      elasticsearch {
          hosts => ["你的elasticsearch服务器地址:9200"]
          index => "app-logstash-%{+YYYY.MM.dd.HH}"
      }
  }
  ```