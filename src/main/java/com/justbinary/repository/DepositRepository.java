package com.justbinary.repository;

import com.justbinary.model.DepositRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface DepositRepository extends MongoRepository<DepositRecord, String> {
    List<DepositRecord> findByUserId(String userId);
    Optional<DepositRecord> findByMpesaCheckoutRequestId(String checkoutRequestId);
}