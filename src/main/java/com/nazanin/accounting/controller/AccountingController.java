package com.nazanin.accounting.controller;

import com.nazanin.accounting.dto.AddMoneyRequestDto;
import com.nazanin.accounting.dto.AddMoneyResponseDto;
import com.nazanin.accounting.dto.GetBalanceResponseDto;
import com.nazanin.accounting.service.AccountingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api/")
@RestController
@RequiredArgsConstructor
public class AccountingController {

    private final AccountingService accountingService;

    @PostMapping("/add-money")
    @ResponseStatus(HttpStatus.OK)
    public AddMoneyResponseDto addMoney(@Valid @RequestBody AddMoneyRequestDto requestDto) {
        return accountingService.addMoney(requestDto);
    }

    @GetMapping("/get-balance/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public GetBalanceResponseDto getBalance(@PathVariable Long userId) {
        return accountingService.getBalance(userId);
    }

}
