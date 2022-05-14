# Logger
## Description
Library, that allow log methods in SpringBoot application 

## Example log messages
```
2022-05-14 12:08:33 INFO  c.h.l.bpp.LogBeanPostProcessor - ***** Class: LocationServiceImpl, method: findLocationById
2022-05-14 12:08:33 DEBUG c.h.l.bpp.LogBeanPostProcessor - ***** arg: 1
2022-05-14 12:08:33 DEBUG c.h.l.bpp.LogBeanPostProcessor - ***** returned value: Location(id=1, locationCountry=Belarus, locationCity=Minsk)
```

- `***** Class: LocationServiceImpl, method: findLocationById` - example log class, where shown class name and method name
Logging level always `INFO`.
- `***** arg: 1` - example logging arguments. Can switch off. Logging level always `DEBUG` 
- `***** returned value: Location(id=1, locationCountry=Belarus, locationCity=Minsk)` - example logging arguments. Can switch off. Logging level always `DEBUG`

## Settings 
1. Since **spring** is used, one of the annotations must be added:

 - @Log - placed above the class and allows you to log all methods
 - @LogMethod - placed above the method and allows you to log specific method

2. Create `logback.xml` file and add appender and logger with package `com.home.logger`:
```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <layout class="ch.qos.logback.classic.PatternLayout">
            <Pattern>
                %d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n
            </Pattern>
        </layout>
    </appender>
    <logger name="com.home.logger" level="debug" additivity="false">
        <appender-ref ref="STDOUT"/>
    </logger>  
</configuration>
```
## Annotations
###### @Log
Annotation that placed above the class and allows to log all methods.
There are two options for setting:
- ***arguments***, default value true. Indicates to log arguments or not
- ***returnValue***, default value true. Indicates to log return value or not

###### @LogMethod
An annotation that is placed above the method and allows you to log specific method.
There are two options for setting:
- ***arguments***, default value true. Indicates to log arguments or not
- ***returnValue***, default value true. Indicates to log return value or not