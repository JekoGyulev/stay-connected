package com.example.stayconnected.aop.aspect;


import com.example.stayconnected.web.dto.property.EditPropertyRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@Slf4j
public class PropertyActionLoggingAspect {


    @After(value = "execution(* com.example.stayconnected.property.service.impl.PropertyServiceImpl.editProperty(..))")
    public void logEditPropertyMethod(JoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();

        UUID propertyId = (UUID) args[0];
        EditPropertyRequest request = (EditPropertyRequest) args[1];

        log.info("Successfully edited property with id [%s] and title [%s]"
                .formatted(propertyId, request.getTitle()));

    }


}
