package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.entity.*;
import com.example.service.*;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class SocialMediaController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper; 

    @PostMapping("register")
    public ResponseEntity<?> registerUser(@RequestBody Account account) {
        // Check for null or empty username and password length
        if (account.getUsername() == null || account.getUsername().isEmpty() || account.getPassword().length() < 4) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Check if username already exists
        if (accountService.existsByUsername(account.getUsername())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        // Save the new account
        Account savedAccount = accountService.save(account);
        return new ResponseEntity<>(savedAccount, HttpStatus.OK);
    }
    
    @PostMapping("login")
    public ResponseEntity<?> loginUser(@RequestBody Account account) {
        Account authenticatedAccount = accountService.authenticate(account.getUsername(), account.getPassword());

        if (authenticatedAccount != null) {
            return ResponseEntity.ok(authenticatedAccount);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("messages")
    public ResponseEntity<?> postMessage(@RequestBody Message message) {
        try {
            Message createdMessage = messageService.createMessage(message);
            if (createdMessage != null) {
                return ResponseEntity.ok(createdMessage);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("messages/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Integer messageId) {
        boolean isDeleted = messageService.deleteMessage(messageId);

        if (isDeleted) {
            return ResponseEntity.ok(1); // 1 row modified
        } else {
            return ResponseEntity.ok().body(""); // No row modified
        }
    }
    
    @PatchMapping("messages/{message_id}")
    public ResponseEntity<?> updateMessage(@PathVariable Integer message_id, @RequestBody String jsonBody) throws JsonMappingException, JsonProcessingException {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonBody);
            JsonNode messageTextNode = rootNode.path("message_text");
            if (messageTextNode.isMissingNode() || messageTextNode.asText().trim().isEmpty()) {
                throw new IllegalArgumentException("Message text is empty");
            }
            String newMessageText = messageTextNode.asText();
            boolean isUpdated = messageService.updateMessage(message_id, newMessageText);
            if (isUpdated) {
                return ResponseEntity.ok(1); // 1 row modified
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("messages/{message_id}")
    public ResponseEntity<Message> getMessageById(@PathVariable Integer message_id) {
        Optional<Message> message = messageService.getMessageById(message_id);

        if (message.isPresent()) {
            return ResponseEntity.ok(message.get());
        } else {
            return ResponseEntity.ok(null);  // or you can choose to return a different status or body if message not found
        }
    }
    
    @GetMapping("/accounts/{account_id}/messages")
    public ResponseEntity<List<Message>> getAllMessagesFromUser(@PathVariable Integer account_id) {
        try {
            List<Message> messages = messageService.findAllByPostedBy(account_id);
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            // Handle the exception based on your application's requirements
            // For example, return a ResponseEntity with an appropriate HTTP status
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}