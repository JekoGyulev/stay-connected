package com.example.stayconnected.aop.aspect;


import com.example.stayconnected.aop.annotations.LogDeletion;
import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.review.model.Review;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class DeletionLoggingAspect {


    @After("@annotation(logDeletion)")
    public void logDeletionOfReview(JoinPoint joinPoint, LogDeletion logDeletion) {

        String entityName = logDeletion.entity();

        Object[] args = joinPoint.getArgs();

        String logMessage = "";

        if (entityName.equals("review")) {
            Review review = (Review) args[0];
            logMessage = "Successfully deleted review with id [%s] that belonged to user with username [%s]"
                    .formatted(review.getId(), review.getCreatedFrom().getUsername());

        } else if (entityName.equals("property")) {
            Property property = (Property) args[0];
            logMessage = "Successfully deleted property with id [%s] and title [%s]"
                    .formatted(property.getId(), property.getTitle());
        }


        log.info(logMessage);

    }



}
