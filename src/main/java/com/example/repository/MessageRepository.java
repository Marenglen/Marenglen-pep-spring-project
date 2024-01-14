package com.example.repository;

import com.example.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    // JpaRepository provides methods like deleteById and existsById.

    // Add any custom query methods if needed.
    // List<Message> findAllByPostedBy(Integer postedBy);
    @Query("SELECT m FROM Message m WHERE m.posted_by = ?1")
    List<Message> findAllByPostedBy(Integer postedBy);
}