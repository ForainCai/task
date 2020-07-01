package com.scheduled.demo.client;

import com.alibaba.fastjson.JSONObject;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.*;
import java.awt.print.Book;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

/**
 * @author caisc
 * @version 1.0.0
 * @ClassName ReadMsg.java
 * @Description TODO
 * @createTime 2020年07月01日 22:11:00
 */
@Component
public class ReadMsg {
    @PostConstruct
    public void scheduledTask() {
        System.out.println("任务执行时间：" + LocalDateTime.now());
        try {
            queueConsumer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static Logger logger = Logger.getLogger(ReadMsg.class.getName());

    @Value("${queueName}")
    private String QUEUENAME;

    @Value("${spring.activemq.user}")
    private String USERNAME;

    @Value("${spring.activemq.password}")
    private String PASSWORD;

    @Value("${spring.activemq.broker-url}")
    private String URL;


    public void queueConsumer() throws Exception {
        logger.info("开始");
        //创建连接工厂对象
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, URL);
        //获取连接对象
        Connection connection = connectionFactory.createConnection();
        //开启连接
        connection.start();
        //使用连接对象获取Session对象,并设置参数
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //使用Session对象创建Destination对象,其中参数为：消息队列的名称
        Queue queue = session.createQueue(QUEUENAME);
        //创建消息消费者对象
        MessageConsumer consumer = session.createConsumer(queue);
        //接收消息
        consumer.setMessageListener(new MessageListener() {
            //接收到消息的事件
            @Override
            public void onMessage(Message message) {
                //简单打印一下
                TextMessage textMessage = (TextMessage) message;
                try {
                    String result = textMessage.getText();
                    logger.info("result");
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
        //接收键盘输入，当在控制台输入回车时结束。（为了让该方法一直处于执行状态）
        System.in.read();
        //关闭资源
        consumer.close();
        session.close();
        connection.close();
    }

}
