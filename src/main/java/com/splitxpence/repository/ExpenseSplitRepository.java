package com.splitxpence.repository;

import com.splitxpence.model.ExpenseSplit;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseSplitRepository
        extends JpaRepository<ExpenseSplit, Long> {

    List<ExpenseSplit> findByExpenseId(Long expenseId);
}