package com.home.logger;

import com.home.logger.annotation.LogMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * BeanPostProcessor that allow to log methods in application
 *
 * @author Siarhei Hrynkou
 *
 */
@Slf4j
@Component
public class LogMethodBeanPostProcessor implements BeanPostProcessor {
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
     * beans whose methods are marked with the {@link com.home.logger.annotation.LogMethod @LogMethod} annotation
     */
    @SuppressWarnings("rawtypes")
    private final Map<String, Class> mapBeans = new HashMap<>();
    /**
     * methods marked with {@link com.home.logger.annotation.LogMethod @LogMethod} annotation
     */
    private final Map<String, List<Method>> mapMethods = new HashMap<>();

    /**
     * <p>Check the methods in the object for the presence of the {@link com.home.logger.annotation.LogMethod @LogMethod} annotation.
     * If a method marked with an annotation is found, the bean and its method are stored in maps
     * </p>
     * @param bean checked object
     * @param beanName object name
     * @return the bean that was checked
     */
    @Nullable
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        Method[] methods = beanClass.getMethods();
        List<Method> beanMethods = Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(LogMethod.class))
                .collect(Collectors.toList());
        if (!beanMethods.isEmpty()) {
            mapBeans.put(beanName, beanClass);
            mapMethods.put(beanName, beanMethods);
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
    @SuppressWarnings("rawtypes")
    @Nullable
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class beanClass = mapBeans.get(beanName);
        List<Method> methods = mapMethods.get(beanName);
        if (beanClass != null) {
            return Proxy
                    .newProxyInstance(beanClass.getClassLoader(), beanClass.getInterfaces(), (proxy, method, args) -> {
                        if (isMethodAnnotated(methods, method)) {
                            return logMethod(beanClass.getSimpleName(), method, args, bean);
                        }
                        return method.invoke(bean, args);
                    });
        }
        return bean;
    }

    private Object logMethod(String simpleName, Method method, Object[] args, Object bean) throws InvocationTargetException, IllegalAccessException {
        log.info(LOG_METHOD_STRING, simpleName, method.getName());
        Arrays.stream(args).forEach(arg -> log.debug(LOG_ARG_STRING, arg));
        Object returnValue = method.invoke(bean, args);
        log.debug(LOG_RETURN_VALUE_STRING, returnValue);
        return returnValue;
    }

    private boolean isMethodAnnotated(List<Method> methods, Method method) {
        return methods.stream()
                .anyMatch(method1 -> method1.getReturnType().equals(method.getReturnType())
                                     && Arrays.equals(method1.getParameterTypes(), method.getParameterTypes()));
    }
}
