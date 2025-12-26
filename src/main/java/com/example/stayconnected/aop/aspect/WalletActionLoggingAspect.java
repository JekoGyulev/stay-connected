package com.example.stayconnected.aop.aspect;

import com.example.stayconnected.reservation.client.dto.CreateReservationRequest;
import com.example.stayconnected.transaction.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Aspect
@Component
@Slf4j
public class WalletActionLoggingAspect {

    @AfterReturning(
            pointcut = "execution(* com.example.stayconnected.wallet.service.impl.WalletServiceImpl.topUp(..))" +
                    " && args(walletId, topUpAmount, ..)",
            returning = "result"
    )
    public void logTopUpMethod(UUID walletId, BigDecimal topUpAmount, Transaction result) {
        log.info("Successfully added %.2f to wallet with id [%s]. Current balance: %.2f"
                .formatted(topUpAmount, walletId, result.getBalanceLeft()));
    }


    @After(value = "execution(* com.example.stayconnected.wallet.service.impl.WalletServiceImpl.refund(..))")
    public void logRefundMethod(JoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();
        UUID userId = (UUID) args[0];
        BigDecimal totalPrice = (BigDecimal) args[1];

        log.info("Successfully refunded amount %.2f for wallet that belongs to user with id [%s]"
                .formatted(totalPrice, userId));
    }

    @After(value = "execution(* com.example.stayconnected.wallet.service.impl.WalletServiceImpl.exchange(..))")
    public void logExchangeMethod(JoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();

        CreateReservationRequest request = (CreateReservationRequest) args[0];
        UUID ownerId = (UUID) args[1];


        log.info("Successfully exchanged money [%.2f] between property reserver with id [%s] and property owner with id [%s]"
                .formatted(request.getTotalPrice(), request.getUserId(), ownerId));
    }

    @After(value = "execution(* com.example.stayconnected.wallet.service.impl.WalletServiceImpl.reverseEarning(..))")
    public void logReverseEarningMethod(JoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();
        BigDecimal totalPrice = (BigDecimal) args[0];
        UUID propertyId = (UUID) args[1];


        log.info("Successfully reversed earning of %.2f for owner of property with id [%s] "
                .formatted(totalPrice, propertyId));

    }

}
