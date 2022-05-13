package com.home.logger;

import com.home.logger.annotation.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class LogClassBeanPostProcessor implements BeanPostProcessor {
    private static final String LOG_METHOD_STRING = "    [Class: {}][method: {}]";
    private static final String LOG_ARG_STRING = "      [arg: {}]";
    private static final String LOG_RETURN_VALUE_STRING = "      [returned value: {}]";
    @SuppressWarnings("rawtypes")
    private final Map<String, Class> map = new HashMap<>();

    @Nullable
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(Log.class)) {
            map.put(beanName, beanClass);
        }
        return bean;
    }

    @SuppressWarnings({"rawtypes"})
    @Nullable
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class beanClass = map.get(beanName);
        if (beanClass != null) {
            return Proxy
                    .newProxyInstance(beanClass.getClassLoader(), beanClass.getInterfaces(), (proxy, method, args) -> {
                        log.info(LOG_METHOD_STRING, beanClass.getSimpleName(), method.getName());
                        Arrays.stream(args).forEach(arg -> log.debug(LOG_ARG_STRING, arg));
                        Object returnValue = method.invoke(bean, args);
                        log.debug(LOG_RETURN_VALUE_STRING, returnValue);
                        return returnValue;
                    });
        }
        return bean;
    }
}
