package com.example.stayconnected.web.dto.transaction;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterTransactionRequest {
    private String transactionType;
    private String transactionStatus;
}
