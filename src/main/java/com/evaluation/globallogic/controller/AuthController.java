package com.evaluation.globallogic.controller;

import com.evaluation.globallogic.config.JwtProvider;
import com.evaluation.globallogic.dto.AccountDTO;
import com.evaluation.globallogic.dto.AccountWithTokenDTO;
import com.evaluation.globallogic.entity.Account;
import com.evaluation.globallogic.exception.UserAlreadyExistsException;
import com.evaluation.globallogic.mapper.AccountMapper;

import com.evaluation.globallogic.service.AccountService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class AuthController {

    private final AccountService accountService;

    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(AccountService accountService,
                          JwtProvider jwtProvider,
                          PasswordEncoder passwordEncoder) {
        this.accountService = accountService;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(value = "/sign-up",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountWithTokenDTO> signUp(@RequestBody @Valid AccountDTO accountDTO) {
        val account = createAccount(accountDTO);
        val token = jwtProvider.generateToken(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(AccountMapper.INSTANCE.accountWithTokenDTO(account, token));
    }

    private Account createAccount(AccountDTO accountDTO) {
        val email = accountDTO.getEmail();
        if (this.accountService.exists(email))
            throw new UserAlreadyExistsException(email);

        val account = new Account(accountDTO.getName(), email, passwordEncoder.encode(accountDTO.getPassword()));
        val phones = Optional.ofNullable(accountDTO.getPhones())
                .orElse(Collections.emptySet())
                .stream()
                .map(phoneDTO -> AccountMapper.INSTANCE.toPhoneEntity(phoneDTO,account))
                .collect(Collectors.toSet());
        account.setPhones(phones);
        account.setActive(true);
        return this.accountService.save(account);
    }

    @GetMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountWithTokenDTO> login() {
        val userName = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        val account = this.accountService.getAccount(userName);
        account.setLastLogin(new Date());
        val savedAccount = this.accountService.save(account);
        val token = jwtProvider.generateToken(savedAccount);
        return ResponseEntity.status(HttpStatus.OK).body(AccountMapper.INSTANCE.accountWithTokenDTO(savedAccount, token));
    }
}
