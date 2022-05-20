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
import java.util.Optional;
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
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Class beanClass = mapBeans.get(beanName);
        List<Method> methods = mapMethods.get(beanName);
        if (beanClass != null) {
            return Proxy
                    .newProxyInstance(beanClass.getClassLoader(), beanClass.getInterfaces(), (proxy, method, args) -> {
                        Optional<Method> originalMethod = getOriginalMethod(methods, method);
                        if (originalMethod.isPresent()) {
                            return logMethod(beanClass.getSimpleName(), originalMethod.get(), method, args, bean);
                        }
                        return method.invoke(bean, args);
                    });
        }
        return bean;
    }

    private Optional<Method> getOriginalMethod(List<Method> methods, Method method) {
        return methods.stream()
                .filter(originalMethod -> originalMethod.getReturnType().equals(method.getReturnType())
                        && Arrays.equals(originalMethod.getParameterTypes(), method.getParameterTypes()))
                .findAny();
    }

    private Object logMethod(String simpleName,Method originalMethod, Method method, Object[] args, Object bean) throws Throwable {
        log.info(LOG_METHOD_STRING, simpleName, method.getName());
        LogMethod annotation = originalMethod.getAnnotation(LogMethod.class);
        if (annotation.arguments()) {
            Arrays.stream(args)
                    .forEach(arg -> log.debug(LOG_ARG_STRING, arg));
        }

        Object returnValue = executeMethod(method, bean, args);

        if (annotation.returnValue()) {
            log.debug(LOG_RETURN_VALUE_STRING, returnValue);
        }
        return returnValue;
    }

    private Object executeMethod(Method method, Object bean, Object[] args) throws Throwable {
        try {
            return method.invoke(bean, args);
        } catch (InvocationTargetException exception) {
            throw exception.getTargetException();
        }
    }
}
