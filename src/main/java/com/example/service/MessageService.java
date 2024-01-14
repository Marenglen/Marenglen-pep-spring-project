package com.example.service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;
import com.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Message createMessage(Message message) {
        if (message.getMessage_text() == null || message.getMessage_text().isEmpty() || message.getMessage_text().length() > 255) {
            throw new IllegalArgumentException("Message text is invalid");
        }

        if (!accountRepository.existsById(message.getPosted_by())) {
            throw new IllegalArgumentException("User ID does not exist");
        }
    
        return messageRepository.save(message);
    }

    public boolean deleteMessage(Integer messageId) {
        if (messageRepository.existsById(messageId)) {
            messageRepository.deleteById(messageId);
            return true;
        } else {
            return false;
        }
    }

    public boolean updateMessage(Integer messageId, String newMessageText) {
        if (newMessageText == null || newMessageText.trim().isEmpty() || newMessageText.length() > 255) {
            throw new IllegalArgumentException("Invalid message text");
        }

        Optional<Message> messageOptional = messageRepository.findById(messageId);
        if (messageOptional.isPresent()) {
            Message message = messageOptional.get();
            message.setMessage_text(newMessageText);
            messageRepository.save(message);
            return true;
        } else {
            return false;
        }
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> getMessageById(Integer messageId) {
        return messageRepository.findById(messageId);
    }

    public List<Message> findAllByPostedBy(Integer accountId) {
        if (accountId == null || !accountRepository.existsById(accountId)) {
            throw new IllegalArgumentException("Invalid or non-existent account ID");
        }
        
        return messageRepository.findAllByPostedBy(accountId);
    }
}