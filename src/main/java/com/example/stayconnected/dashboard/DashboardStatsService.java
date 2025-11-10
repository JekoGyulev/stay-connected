package com.example.stayconnected.dashboard;

import com.example.stayconnected.property.repository.PropertyRepository;
import com.example.stayconnected.reservation.enums.ReservationStatus;
import com.example.stayconnected.reservation.repository.ReservationRepository;
import com.example.stayconnected.transaction.enums.TransactionStatus;
import com.example.stayconnected.transaction.enums.TransactionType;
import com.example.stayconnected.transaction.repository.TransactionRepository;
import com.example.stayconnected.transaction.service.TransactionService;
import com.example.stayconnected.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class DashboardStatsService {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final PropertyRepository propertyRepository;
    private final TransactionRepository  transactionRepository;

    private final TransactionService transactionService;


    @Getter
    private long countNewUsersToday;
    @Getter
    private long countNewReservationsToday;
    @Getter
    private long countNewPropertiesToday;
    @Getter
    private BigDecimal countTotalRevenueToday;
    @Getter
    private BigDecimal averageMonthlyTransactionGrowth;


    @Autowired
    public DashboardStatsService(UserRepository userRepository, ReservationRepository reservationRepository, PropertyRepository propertyRepository, TransactionService transactionService, TransactionRepository transactionRepository, TransactionService transactionService1) {
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.propertyRepository = propertyRepository;
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService1;
    }

    @Scheduled(cron = "0 30 22 * * *")
    public void calculateDailyStats() {

        LocalDate today = LocalDate.now();

        this.countNewUsersToday = this.userRepository.countAllByRegisteredAtBetween(today.atStartOfDay(), today.plusDays(1).atStartOfDay());

        this.countNewReservationsToday = this.reservationRepository.countAllByStatusAndCreatedAtBetween(
                ReservationStatus.PAID,today.atStartOfDay(), today.plusDays(1).atStartOfDay());

        this.countNewPropertiesToday = this.propertyRepository.countAllByCreateDateBetween(today.atStartOfDay(), today.plusDays(1).atStartOfDay());

        this.countTotalRevenueToday = this.transactionRepository.sumAmountByStatusAndTypeInAndCreatedOnBetween(
                TransactionStatus.SUCCEEDED,
                List.of(TransactionType.DEPOSIT, TransactionType.BOOKING_PAYMENT),
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );
    }


    @Scheduled(cron = "0 0 0 1 * ?")
    public void calculateMonthlyAverageTransactionGrowth() {
        LocalDate today = LocalDate.now();

        LocalDateTime startOfCurrentMonth = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime startOfLastMonth = startOfCurrentMonth.minusMonths(1);
        LocalDateTime endOfLastMonth = startOfCurrentMonth;


        BigDecimal lastMonthAverageTransaction = this.transactionService.calculateAverageTransactionAmountByMonth(
                startOfLastMonth,
                endOfLastMonth
        );

        BigDecimal currentMonthAverageTransaction = this.transactionService.calculateAverageTransactionAmountByMonth(
                startOfCurrentMonth,
                LocalDateTime.now()
        );

        if (lastMonthAverageTransaction.compareTo(BigDecimal.ZERO) == 0) {
            averageMonthlyTransactionGrowth = BigDecimal.ZERO;
            return;
        }

        BigDecimal averageGrowth =
                currentMonthAverageTransaction
                        .subtract(lastMonthAverageTransaction)
                        .divide(lastMonthAverageTransaction, 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));


        this.averageMonthlyTransactionGrowth = averageGrowth.setScale(2, RoundingMode.HALF_UP);
    }

}
