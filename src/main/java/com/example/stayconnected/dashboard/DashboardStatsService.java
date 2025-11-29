package com.example.stayconnected.dashboard;

import com.example.stayconnected.property.repository.PropertyRepository;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class DashboardStatsService {

    private final UserRepository userRepository;
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
    private BigDecimal averageWeeklyTransactionGrowth;


    @Autowired
    public DashboardStatsService(UserRepository userRepository,PropertyRepository propertyRepository, TransactionService transactionService, TransactionRepository transactionRepository, TransactionService transactionService1) {
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService1;
    }

    @Scheduled(cron = "0 59 23 * * *")
    public void calculateDailyStats() {

        LocalDate today = LocalDate.now();

        this.countNewUsersToday = this.userRepository.countAllByRegisteredAtBetween(today.atStartOfDay(),
                today.plusDays(1).atStartOfDay());


        this.countNewPropertiesToday = this.propertyRepository.countAllByCreateDateBetween(today.atStartOfDay(),
                today.plusDays(1).atStartOfDay());

        this.countTotalRevenueToday = this.transactionRepository.sumAmountByStatusAndTypeInAndCreatedOnBetween(
                TransactionStatus.SUCCEEDED,
                List.of(TransactionType.DEPOSIT, TransactionType.BOOKING_PAYMENT),
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );
    }


    @Scheduled(cron = "0 0 0 * * MON")
    public void calculateAverageWeeklyTransactionGrowth() {
        LocalDate today = LocalDate.now();

        LocalDate lastWeekStart = today.minusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDate lastWeekEnd = lastWeekStart.plusWeeks(1);

        LocalDate weekBeforeLastStart = lastWeekStart.minusWeeks(1);
        LocalDate weekBeforeLastEnd = lastWeekStart;

        BigDecimal lastWeekAverage = this.transactionService.calculateWeeklyAverageTransactionAmount(
                lastWeekStart.atStartOfDay(),
                lastWeekEnd.atStartOfDay()
        );

        BigDecimal weekBeforeLastAverage = this.transactionService.calculateWeeklyAverageTransactionAmount(
                weekBeforeLastStart.atStartOfDay(),
                weekBeforeLastEnd.atStartOfDay()
        );

        if (weekBeforeLastAverage.compareTo(BigDecimal.ZERO) == 0) {
            averageWeeklyTransactionGrowth = BigDecimal.ZERO;
            return;
        }

        BigDecimal averageGrowth =
                lastWeekAverage
                        .subtract(weekBeforeLastAverage)
                        .divide(weekBeforeLastAverage, 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);


        this.averageWeeklyTransactionGrowth = averageGrowth;
    }

    @PostConstruct
    public void init() {
        calculateDailyStats();
        calculateAverageWeeklyTransactionGrowth();
    }

}
