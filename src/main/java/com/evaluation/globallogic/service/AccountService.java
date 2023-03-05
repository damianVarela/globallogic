package com.evaluation.globallogic.service;

import com.evaluation.globallogic.config.ApplicationUser;
import com.evaluation.globallogic.entity.Account;
import com.evaluation.globallogic.repository.AccountRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        val account = accountRepository.findAccountByEmail(email);
        if (account == null)
            throw new UsernameNotFoundException("account not found.");
        return ApplicationUser.build(account);
    }

    public Account getAccount(String email) {
        return this.accountRepository.findAccountByEmail(email);
    }

    public boolean exists(String email) {
        return this.accountRepository.existsAccountByEmail(email);
    }

    public Account save(Account account) {
        return this.accountRepository.save(account);
    }



}
