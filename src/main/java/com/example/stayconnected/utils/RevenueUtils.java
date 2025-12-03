package com.example.stayconnected.utils;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class RevenueUtils {

    public String formatRevenue(BigDecimal revenue) {
        if (revenue.compareTo(BigDecimal.valueOf(1_000_000)) >= 0) {
            return String.format("€%.1fM", revenue.doubleValue() / 1_000_000);
        } else if (revenue.compareTo(BigDecimal.valueOf(1_000)) >= 0) {
            return String.format("€%.1fK", revenue.doubleValue() / 1_000);
        } else {
            return String.format("€%.2f", revenue);
        }
    }
}
