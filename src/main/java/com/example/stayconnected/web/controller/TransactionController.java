package com.example.stayconnected.web.controller;

import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.transaction.model.Transaction;
import com.example.stayconnected.transaction.service.TransactionService;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.web.dto.transaction.FilterTransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


import java.util.UUID;

@Controller
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final UserService userService;

    @Autowired
    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getTransactionsPage(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                                            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());

        Page<Transaction> transactions = this.transactionService.getTransactionsByUserId(user.getId(), pageNumber, pageSize);

        int totalPages = transactions.getTotalPages();
        long totalElements = transactions.getTotalElements();

        String baseUrl = "/transactions";
        String queryParameters = "";


        ModelAndView modelAndView = new ModelAndView("/transaction/transactions");
        modelAndView.addObject("user", user);
        modelAndView.addObject("transactions", transactions);
        modelAndView.addObject("filterTransaction", new FilterTransactionRequest());
        modelAndView.addObject("totalPages", totalPages);
        modelAndView.addObject("totalElements", totalElements);
        modelAndView.addObject("currentPage", pageNumber);
        modelAndView.addObject("pageSize", pageSize);
        modelAndView.addObject("baseUrl", baseUrl);
        modelAndView.addObject("queryParameters", queryParameters);

        return modelAndView;
    }

    @GetMapping("/filter")
    public ModelAndView showFilteredPage(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                         @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                                         FilterTransactionRequest request,
                                         @AuthenticationPrincipal UserPrincipal userPrincipal
                                         ) {

        User user = this.userService.getUserById(userPrincipal.getId());

        Page<Transaction> filteredTransactions = this.transactionService
                .getFilteredTransactions(user.getId(), request, pageNumber, pageSize);

        int totalPages = filteredTransactions.getTotalPages();
        long totalElements = filteredTransactions.getTotalElements();


        String baseUrl = "/transactions/filter";
        String queryParameters = "&transactionType=%s&transactionStatus=%s"
                .formatted(request.getTransactionType(), request.getTransactionStatus());

        ModelAndView modelAndView = new ModelAndView("transaction/transactions");
        modelAndView.addObject("transactions", filteredTransactions);
        modelAndView.addObject("filterTransaction", request);
        modelAndView.addObject("user", user);
        modelAndView.addObject("totalPages", totalPages);
        modelAndView.addObject("totalElements", totalElements);
        modelAndView.addObject("currentPage", pageNumber);
        modelAndView.addObject("pageSize", pageSize);
        modelAndView.addObject("baseUrl", baseUrl);
        modelAndView.addObject("queryParameters", queryParameters);

        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getTransactionDetails(@PathVariable UUID id,
                                              @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());

        Transaction transaction = this.transactionService.getTransactionById(id);

        ModelAndView modelAndView = new ModelAndView("transaction/transaction-details");
        modelAndView.addObject("user", user);
        modelAndView.addObject("transaction", transaction);

        return modelAndView;
    }


}
