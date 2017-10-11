package com.collapsar.springboot.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.ClientConfig;
import org.apache.rocketmq.client.consumer.AllocateMessageQueueStrategy;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.store.OffsetStore;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.UtilAll;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyong6 on 2017/8/30.
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix="spring.rocketmq")
public class MyRocketConfig {
    private String namesrvAddr;
    private Producer producer;
    private Consumer consumer;
    private Template template;
    @Autowired
    private ApplicationEventPublisher publisher;


    @PostConstruct
    public void init() throws Exception{
//        log.debug("init: biztypes={}, dburls={}", StringUtils.collectionToCommaDelimitedString(biztypes), StringUtils.collectionToCommaDelimitedString(dburls));
        log.info("#####RocketMq init.");
    }


    @Bean
    public DefaultMQProducer defaultMQProducer() throws MQClientException {
        final DefaultMQProducer producer = new DefaultMQProducer(this.producer.getProducerGroup());
        producer.setNamesrvAddr(namesrvAddr);
        producer.setVipChannelEnabled(false);
        producer.start();
        log.info("#####RocketMq defaultProducer started.");
        return producer;
    }

    @Bean
    public DefaultMQPushConsumer defaultMQPushConsumer() throws MQClientException{
        final DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(this.consumer.getConsumerGroup());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setConsumeMessageBatchMaxSize(1);

        for(String subcribe : this.consumer.getSubscription()){
            String[] topicAndTag = subcribe.split(":");
            consumer.subscribe(topicAndTag[0], topicAndTag[1]);
        }
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                MessageExt msg = msgs.get(0);
                try{
                    log.info("#####RocketMq defaultConsumer receive msg={}", msg);
                    publisher.publishEvent(new RocketmqEvent(msg, consumer));

                }catch (Exception ex){
                    log.error("#####RocketMq publishEvent err.", ex);
                    if(msg.getReconsumeTimes() <= 3){
                        log.debug("#####RocketMq msg will reconsume again. has retry {} times.", msg.getReconsumeTimes());
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }else{
                        log.debug("#####RocketMq consume err. has retry {} times.", msg.getReconsumeTimes());
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
        log.info("#####RocketMq defaultConsumer started.");
        return consumer;
    }


    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class RocketmqEvent extends ApplicationEvent implements Serializable{
        private DefaultMQPushConsumer consumer;
        private MessageExt msg;
        private String topic;
        private String tag;
        private byte[] body;

        public RocketmqEvent(MessageExt msg, DefaultMQPushConsumer consumer) throws Exception{
            super(msg);
            this.consumer = consumer;
            this.msg = msg;
            this.topic = msg.getTopic();
            this.tag = msg.getTags();
            this.body = msg.getBody();
        }
    }


    @Data
    public static class Producer{
        /**
         * Producer group conceptually aggregates all producer instances of exactly same role, which is particularly
         * important when transactional messages are involved.
         * </p>
         *
         * For non-transactional messages, it does not matter as long as it's unique per process.
         * </p>
         *
         * See {@linktourl http://rocketmq.incubator.apache.org/docs/core-concept/} for more discussion.
         */
        private String producerGroup;

        /**
         * Just for testing or demo program
         */
        private String createTopicKey = MixAll.DEFAULT_TOPIC;

        /**
         * Number of queues to create per default topic.
         */
        private volatile int defaultTopicQueueNums = 4;

        /**
         * Timeout for sending messages.
         */
        private int sendMsgTimeout = 3000;

        /**
         * Compress message body threshold, namely, message body larger than 4k will be compressed on default.
         */
        private int compressMsgBodyOverHowmuch = 1024 * 4;

        /**
         * Maximum number of retry to perform internally before claiming sending failure in synchronous mode.
         * </p>
         *
         * This may potentially cause message duplication which is up to application developers to resolve.
         */
        private int retryTimesWhenSendFailed = 2;

        /**
         * Maximum number of retry to perform internally before claiming sending failure in asynchronous mode.
         * </p>
         *
         * This may potentially cause message duplication which is up to application developers to resolve.
         */
        private int retryTimesWhenSendAsyncFailed = 2;

        /**
         * Indicate whether to retry another broker on sending failure internally.
         */
        private boolean retryAnotherBrokerWhenNotStoreOK = false;

        /**
         * Maximum allowed message size in bytes.
         */
        private int maxMessageSize = 1024 * 1024 * 4; // 4M
    }

    @Data
    public static class Consumer{
        /**
         * Consumers of the same role is required to have exactly same subscriptions and consumerGroup to correctly achieve
         * load balance. It's required and needs to be globally unique.
         * </p>
         *
         * See <a href="http://rocketmq.incubator.apache.org/docs/core-concept/">here</a> for further discussion.
         */
        private String consumerGroup;

        /**
         * Message model defines the way how messages are delivered to each consumer clients.
         * </p>
         *
         * RocketMQ supports two message models: clustering and broadcasting. If clustering is set, consumer clients with
         * the same {@link #consumerGroup} would only consume shards of the messages subscribed, which achieves load
         * balances; Conversely, if the broadcasting is set, each consumer client will consume all subscribed messages
         * separately.
         * </p>
         *
         * This field defaults to clustering.
         */
        private MessageModel messageModel = MessageModel.CLUSTERING;

        /**
         * Consuming point on consumer booting.
         * </p>
         *
         * There are three consuming points:
         * <ul>
         *     <li>
         *         <code>CONSUME_FROM_LAST_OFFSET</code>: consumer clients pick up where it stopped previously.
         *         If it were a newly booting up consumer client, according aging of the consumer group, there are two
         *         cases:
         *         <ol>
         *             <li>
         *                 if the consumer group is created so recently that the earliest message being subscribed has yet
         *                 expired, which means the consumer group represents a lately launched business, consuming will
         *                 start from the very beginning;
         *             </li>
         *             <li>
         *                 if the earliest message being subscribed has expired, consuming will start from the latest
         *                 messages, meaning messages born prior to the booting timestamp would be ignored.
         *             </li>
         *         </ol>
         *     </li>
         *     <li>
         *         <code>CONSUME_FROM_FIRST_OFFSET</code>: Consumer client will start from earliest messages available.
         *     </li>
         *     <li>
         *         <code>CONSUME_FROM_TIMESTAMP</code>: Consumer client will start from specified timestamp, which means
         *         messages born prior to {@link #consumeTimestamp} will be ignored
         *     </li>
         * </ul>
         */
        private ConsumeFromWhere consumeFromWhere = ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET;

        /**
         * Backtracking consumption time with second precision. Time format is
         * 20131223171201<br>
         * Implying Seventeen twelve and 01 seconds on December 23, 2013 year<br>
         * Default backtracking consumption time Half an hour ago.
         */
        private String consumeTimestamp = UtilAll.timeMillisToHumanString3(System.currentTimeMillis() - (1000 * 60 * 30));

        /**
         * Queue allocation algorithm specifying how message queues are allocated to each consumer clients.
         */
        private AllocateMessageQueueStrategy allocateMessageQueueStrategy;

        /**
         * Subscription relationship
         */
//        private Map<String /* topic */, String /* sub expression */> subscription = new HashMap<String, String>();
        private List<String> subscription;

        /**
         * Message listener
         */
        private MessageListener messageListener;

        /**
         * Offset Storage
         */
        private OffsetStore offsetStore;

        /**
         * Minimum consumer thread number
         */
        private int consumeThreadMin = 20;

        /**
         * Max consumer thread number
         */
        private int consumeThreadMax = 64;

        /**
         * Threshold for dynamic adjustment of the number of thread pool
         */
        private long adjustThreadPoolNumsThreshold = 100000;

        /**
         * Concurrently max span offset.it has no effect on sequential consumption
         */
        private int consumeConcurrentlyMaxSpan = 2000;

        /**
         * Flow control threshold
         */
        private int pullThresholdForQueue = 1000;

        /**
         * Message pull Interval
         */
        private long pullInterval = 0;

        /**
         * Batch consumption size
         */
        private int consumeMessageBatchMaxSize = 1;

        /**
         * Batch pull size
         */
        private int pullBatchSize = 32;

        /**
         * Whether update subscription relationship when every pull
         */
        private boolean postSubscriptionWhenPull = false;

        /**
         * Whether the unit of subscription group
         */
        private boolean unitMode = false;

        /**
         * Max re-consume times. -1 means 16 times.
         * </p>
         *
         * If messages are re-consumed more than {@link #maxReconsumeTimes} before success, it's be directed to a deletion
         * queue waiting.
         */
        private int maxReconsumeTimes = -1;

        /**
         * Suspending pulling time for cases requiring slow pulling like flow-control scenario.
         */
        private long suspendCurrentQueueTimeMillis = 1000;

        /**
         * Maximum amount of time in minutes a message may block the consuming thread.
         */
        private long consumeTimeout = 15;
    }

    @Data
    public static class Template{
        private String defaultSubscription;
    }
}
