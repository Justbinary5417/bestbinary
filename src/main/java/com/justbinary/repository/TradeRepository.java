package com.justbinary.repository;

import com.justbinary.model.Trade;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TradeRepository 
        extends MongoRepository<Trade, String> {

    List<Trade> findByUserId(String userId);
    List<Trade> findByStatus(String status);
    List<Trade> findByUserIdAndStatus(String userId, String status); // ← ADD
}