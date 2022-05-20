package com.home.logger.wrappedclass;

import com.home.logger.annotation.Log;
import com.home.logger.exception.TestException;
import org.springframework.stereotype.Component;

@Log
@Component
public class LogClassWrappedImpl implements LogWrapped {
    @Override
    public Object getObject(Object object) {
        if (object == null) {
            throw new TestException("Object is null");
        }
        return object;
    }
}
