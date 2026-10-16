package com.splitxpence.repository;

import com.splitxpence.model.Settlement;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository
        extends JpaRepository<Settlement, Long> {

    List<Settlement> findByGroupId(Long groupId);
}