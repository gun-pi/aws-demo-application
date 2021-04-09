package com.example.demo.mq.configs;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;


@Configuration
public class JmsConfig {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${spring.activemq.user}")
    private String brokerUser;

    @Value("${spring.activemq.password}")
    private String brokerPassword;

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUser, brokerPassword, brokerUrl);
        return new CachingConnectionFactory(factory);
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(cachingConnectionFactory());
    }

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        final ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(brokerUser, brokerPassword, brokerUrl);
        activeMQConnectionFactory.setTrustAllPackages(true);
        return activeMQConnectionFactory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(activeMQConnectionFactory());
        return factory;
    }
}

