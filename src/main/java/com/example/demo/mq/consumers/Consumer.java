package com.example.demo.mq.consumers;

import com.example.demo.business.models.Document;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;

@Component
@PropertySource("classpath:application.properties")
public class Consumer {

    private static final Logger LOG = LoggerFactory.getLogger(Consumer.class);

    @JmsListener(destination = "${destination}")
    public void receive(Object activeMQObjectMessage) {
        Document message;
        try {
            message = (Document) ((ActiveMQObjectMessage) activeMQObjectMessage).getObject();
        } catch (JMSException e) {
            LOG.error("JMSException occurred in Consumer: ", e);
            throw new RuntimeException(e);
        }
        LOG.info("File saved {} {} with id: {}", message.getContent(), message, message.getId());
    }
}
