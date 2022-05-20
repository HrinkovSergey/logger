package com.home.logger.wrappedclass;

import com.home.logger.config.TestConfig;
import com.home.logger.exception.TestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = TestConfig.class)
class LogMethodWrappedImplTest {

    @Autowired
    private LogWrapped logMethodWrappedImpl;

    @Test
    void logMethodWrapped_all_are_ok_getObject() {
        Object object = new Object();

        Object resultObject = logMethodWrappedImpl.getObject(object);

        assertEquals(object, resultObject);
    }

    @Test
    void logMethodWrapped_object_empty_getObject() {
        Object object = null;

        //noinspection ConstantConditions
        assertThrows(TestException.class, () -> logMethodWrappedImpl.getObject(object));
    }
}