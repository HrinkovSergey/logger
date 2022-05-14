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

/**
 * BeanPostProcessor that allow to log whole class in application
 *
 * @author Siarhei Hrynkou
 *
 */
@Slf4j
@Component
public class LogClassBeanPostProcessor implements BeanPostProcessor {
    /**
     * class and method logging message
     */
    private static final String LOG_METHOD_STRING = "***** Class: {}, method: {}";
    /**
     * args logging message
     */
    private static final String LOG_ARG_STRING = "***** arg: {}";
    /**
     * return value logging message
     */
    private static final String LOG_RETURN_VALUE_STRING = "***** returned value: {}";
    /**
     * beans are marked with the {@link com.home.logger.annotation.LogMethod @LogMethod} annotation
     */
    @SuppressWarnings("rawtypes")
    private final Map<String, Class> map = new HashMap<>();

    /**
     * <p>Check the object for the presence of the {@link com.home.logger.annotation.LogMethod @LogMethod} annotation.
     * If the bean marked with an annotation, the bean is stored in maps
     * </p>
     * @param bean checked object
     * @param beanName object name
     * @return the bean that was checked
     */
    @Nullable
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(Log.class)) {
            map.put(beanName, beanClass);
        }
        return bean;
    }

    /**
     * <p>Log the methods in the object for the presence of the {@link com.home.logger.annotation.LogMethod @LogMethod} annotation.
     * </p>
     * @param bean checked object
     * @param beanName object name
     * @return proxy of bean if it method marked {@link com.home.logger.annotation.LogMethod @LogMethod} annotation
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Nullable
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class beanClass = map.get(beanName);
        if (beanClass != null) {
            return Proxy
                    .newProxyInstance(beanClass.getClassLoader(), beanClass.getInterfaces(), (proxy, method, args) -> {
                        log.info(LOG_METHOD_STRING, beanClass.getSimpleName(), method.getName());
                        Log annotation = (Log) beanClass.getAnnotation(Log.class);
                        if (annotation.arguments()) {
                            Arrays.stream(args)
                                    .forEach(arg -> log.debug(LOG_ARG_STRING, arg));
                        }

                        Object returnValue = method.invoke(bean, args);

                        if (annotation.returnValue()) {
                            log.debug(LOG_RETURN_VALUE_STRING, returnValue);
                        }
                        return returnValue;
                    });
        }
        return bean;
    }
}
