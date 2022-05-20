package com.home.logger.wrappedclass;

import com.home.logger.annotation.LogMethod;
import com.home.logger.exception.TestException;
import org.springframework.stereotype.Component;

@Component
public class LogMethodWrappedImpl implements LogWrapped {

    @LogMethod
    @Override
    public Object getObject(Object object) {
        if (object == null) {
            throw new TestException("Object is null");
        }
        return object;
    }
}
