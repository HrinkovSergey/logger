package com.home.logger.wrappedclass;

import com.home.logger.config.TestConfig;
import com.home.logger.exception.TestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = TestConfig.class)
class LogClassWrappedImplTest {

    @Autowired()
    private LogWrapped logClassWrappedImpl;

    @Test
    void LogClassWrappedImplTest_all_are_ok_getObject() {
        Object object = new Object();

        Object resultObject = logClassWrappedImpl.getObject(object);

        assertEquals(object, resultObject);
    }

    @Test
    void LogClassWrappedImplTest_object_empty_getObject() {
       Object object = null;

        //noinspection ConstantConditions
        assertThrows(TestException.class, () -> logClassWrappedImpl.getObject(object));
    }
}