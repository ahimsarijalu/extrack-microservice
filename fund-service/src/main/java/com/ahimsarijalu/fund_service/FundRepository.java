package com.ahimsarijalu.fund_service;

import com.ahimsarijalu.fund_service.model.Fund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FundRepository extends JpaRepository<Fund, UUID> {
    List<Fund> findAllByUserId(UUID userId);

    Fund findByUserId(UUID userId);
}
