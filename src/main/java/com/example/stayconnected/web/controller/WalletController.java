package com.example.stayconnected.web.controller;

import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.transaction.enums.TransactionType;
import com.example.stayconnected.transaction.model.Transaction;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.UUID;


@Controller
public class WalletController {

    private final UserService userService;
    private final WalletService walletService;

    @Autowired
    public WalletController(UserService userService, WalletService walletService) {
        this.userService = userService;
        this.walletService = walletService;
    }

    @PatchMapping("/wallet/top-up")
    public String topUpWallet(  @RequestParam(name = "amount") BigDecimal amount,
                                @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());
        UUID walletID = user.getWallet().getId();

        Transaction transaction = this.walletService.topUp(walletID, amount, TransactionType.DEPOSIT);

        return "redirect:/transactions/" + transaction.getId();
    }
}
