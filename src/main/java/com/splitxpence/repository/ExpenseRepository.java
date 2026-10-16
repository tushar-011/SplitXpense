package com.splitxpence.repository;

import com.splitxpence.model.Expense;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByGroupId(Long groupId);

    List<Expense> findByTitleContainingIgnoreCase(String title);

    List<Expense> findByCategoryIgnoreCase(String category);

    List<Expense> findByExpenseDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

    List<Expense> findByGroupIdAndCategoryIgnoreCase(
            Long groupId,
            String category
    );

    List<Expense> findByGroupIdAndTitleContainingIgnoreCase(
            Long groupId,
            String title
    );

    List<Expense> findByGroupIdAndExpenseDateBetween(
            Long groupId,
            LocalDate startDate,
            LocalDate endDate
    );
}