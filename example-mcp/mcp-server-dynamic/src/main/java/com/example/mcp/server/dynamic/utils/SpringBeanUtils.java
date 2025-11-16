package com.example.mcp.server.dynamic.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

public final class SpringBeanUtils {
    private static final SpringBeanUtils INSTANCE = new SpringBeanUtils();

    private ApplicationContext applicationContext;

    private SpringBeanUtils() {
    }

    public static SpringBeanUtils getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("all")
    public <T> T getBean(final String beanName) {
        return (T) applicationContext.getBean(beanName);
    }

    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return applicationContext.getBean(name, requiredType);
    }

    public void registerSingleton(String beanName, Object singletonObject) {
        DefaultListableBeanFactory beanFactory = getBeanFactory();
        beanFactory.registerSingleton(beanName, singletonObject);
    }

    public void destroySingleton(String beanName) {
        DefaultListableBeanFactory beanFactory = getBeanFactory();
        beanFactory.destroySingleton(beanName);
    }

    public boolean containsSingleton(String beanName) {
        DefaultListableBeanFactory beanFactory = getBeanFactory();
        return beanFactory.containsSingleton(beanName);
    }

    private DefaultListableBeanFactory getBeanFactory() {
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        return (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
    }

    public void setApplicationContext(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
