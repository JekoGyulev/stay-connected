package com.example.stayconnected.web.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilterTransactionRequest {
    private String transactionType;
    private String transactionStatus;
}
