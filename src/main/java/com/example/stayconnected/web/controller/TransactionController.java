package com.example.stayconnected.web.controller;

import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.transaction.model.Transaction;
import com.example.stayconnected.transaction.service.TransactionService;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
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
    public ModelAndView getTransactionsPage(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());

        List<Transaction> transactions = this.transactionService.getTransactionsByUserId(user.getId());

        ModelAndView modelAndView = new ModelAndView("/transaction/transactions");
        modelAndView.addObject("user", user);
        modelAndView.addObject("transactions", transactions);



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
