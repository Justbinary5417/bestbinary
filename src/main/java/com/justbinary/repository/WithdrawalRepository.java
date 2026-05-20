package com.justbinary.repository;

import com.justbinary.model.WithdrawalRecord;
import com.justbinary.model.WithdrawalRecord.Status;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface WithdrawalRepository 
        extends MongoRepository<WithdrawalRecord, String> {

    List<WithdrawalRecord> findByStatus(Status status);

    List<WithdrawalRecord> findByUserId(String userId);

    List<WithdrawalRecord> findAllByOrderByRequestedAtDesc();
}