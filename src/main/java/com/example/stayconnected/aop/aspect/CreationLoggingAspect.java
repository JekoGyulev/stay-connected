package com.example.stayconnected.aop.aspect;


import com.example.stayconnected.aop.annotations.LogCreation;
import com.example.stayconnected.location.model.Location;
import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.model.PropertyImage;
import com.example.stayconnected.review.model.Review;
import com.example.stayconnected.transaction.model.Transaction;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.wallet.model.Wallet;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class CreationLoggingAspect {


    @AfterReturning(
            pointcut = "@annotation(logCreation)",
            returning = "result"
    )
    public void logCreationMethod(Object result, LogCreation logCreation) {

        String entityName = logCreation.entity();

        if (result != null) {

            String logMessage = switch (entityName)

            {
                case "user" -> "Successfully registered user with id [%s] and username [%s]"
                        .formatted( ((User) result).getId(), ((User) result).getUsername());
                case "wallet" -> "Successfully created a wallet with id [%s] for user id [%s]"
                        .formatted( ((Wallet) result).getId(), ((Wallet) result).getOwner().getId());
                case "review" -> "Successfully added review with id [%s] from user with id [%s] to property with id [%s]"
                        .formatted( ((Review) result).getId(), ((Review) result).getCreatedFrom().getId(),
                                    ((Review) result).getProperty().getId());
                case "transaction" -> "Successfully created transaction with id [%s] for user [%s]"
                        .formatted( ((Transaction) result).getId(), ((Transaction) result).getOwner().getId());

                case "property" -> "Successfully added property with id [%s] and title [%s]"
                        .formatted( ((Property) result).getId(), ((Property) result).getTitle());

                case "location" -> "Successfully created location with id [%s]"
                        .formatted( ((Location) result).getId());

                case "property image" -> "Successfully created image with id [%s] for property with id [%s]"
                        .formatted( ((PropertyImage) result).getId(), ((PropertyImage) result).getProperty().getId());

                default -> "Successfully created " + entityName;
            };



            log.info(logMessage);
        }


    }

}
