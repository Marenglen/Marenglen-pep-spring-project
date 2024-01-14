package com.example.service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public boolean existsByUsername(String username) {
        // Implement logic to check if an account with the given username exists
        return accountRepository.findByUsername(username).isPresent();
    }

    public Account save(Account account) {
        // Implement logic to save a new account
        return accountRepository.save(account);
    }

    public Account authenticate(String username, String password) {
        return accountRepository.findByUsernameAndPassword(username, password)
                                .orElse(null);
    }
}