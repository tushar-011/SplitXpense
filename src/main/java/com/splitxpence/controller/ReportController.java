package com.splitxpence.controller;

import com.splitxpence.model.Expense;
import com.splitxpence.model.ExpenseGroup;

import com.splitxpence.repository.ExpenseRepository;
import com.splitxpence.repository.GroupMemberRepository;
import com.splitxpence.repository.GroupRepository;

import jakarta.servlet.http.HttpSession;

import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportController {

    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public ReportController(
            ExpenseRepository expenseRepository,
            GroupRepository groupRepository,
            GroupMemberRepository groupMemberRepository) {

        this.expenseRepository = expenseRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    @GetMapping("/group-summary")
    public Object groupSummary(
            @RequestParam Long groupId,
            HttpSession session) {

        Object validation =
                validateGroupAccess(groupId, session);

        if (validation instanceof String) {
            return validation;
        }

        ExpenseGroup group =
                (ExpenseGroup) validation;

        List<Expense> expenses =
                expenseRepository.findByGroupId(groupId);

        double total = 0.0;

        for (Expense expense : expenses) {
            total += expense.getAmount();
        }

        Map<String, Object> report =
                new LinkedHashMap<>();

        report.put(
                "groupId",
                group.getId()
        );

        report.put(
                "groupName",
                group.getName()
        );

        report.put(
                "numberOfExpenses",
                expenses.size()
        );

        report.put(
                "totalExpense",
                round(total)
        );

        return report;
    }

    @GetMapping("/category-report")
    public Object categoryReport(
            @RequestParam Long groupId,
            HttpSession session) {

        Object validation =
                validateGroupAccess(groupId, session);

        if (validation instanceof String) {
            return validation;
        }

        List<Expense> expenses =
                expenseRepository.findByGroupId(groupId);

        Map<String, Double> categoryTotals =
                new LinkedHashMap<>();

        for (Expense expense : expenses) {

            String category =
                    expense.getCategory();

            double current =
                    categoryTotals.getOrDefault(
                            category,
                            0.0
                    );

            categoryTotals.put(
                    category,
                    current + expense.getAmount()
            );
        }

        categoryTotals.replaceAll(
                (category, total) -> round(total)
        );

        return categoryTotals;
    }

    @GetMapping("/monthly-report")
    public Object monthlyReport(
            @RequestParam Long groupId,
            HttpSession session) {

        Object validation =
                validateGroupAccess(groupId, session);

        if (validation instanceof String) {
            return validation;
        }

        List<Expense> expenses =
                expenseRepository.findByGroupId(groupId);

        Map<String, Double> monthlyTotals =
                new TreeMap<>();

        for (Expense expense : expenses) {

            YearMonth month =
                    YearMonth.from(
                            expense.getExpenseDate()
                    );

            String monthName =
                    month.toString();

            double current =
                    monthlyTotals.getOrDefault(
                            monthName,
                            0.0
                    );

            monthlyTotals.put(
                    monthName,
                    current + expense.getAmount()
            );
        }

        monthlyTotals.replaceAll(
                (month, total) -> round(total)
        );

        return monthlyTotals;
    }

    private Object validateGroupAccess(
            Long groupId,
            HttpSession session) {

        Long userId =
                (Long) session.getAttribute("userId");

        if (userId == null) {
            return "Please login first";
        }

        Optional<ExpenseGroup> groupOptional =
                groupRepository.findById(groupId);

        if (groupOptional.isEmpty()) {
            return "Group not found";
        }

        ExpenseGroup group =
                groupOptional.get();

        boolean isCreator =
                group.getCreatedBy()
                        .getId()
                        .equals(userId);

        boolean isMember =
                groupMemberRepository
                        .findByGroupIdAndUserId(
                                groupId,
                                userId
                        )
                        .isPresent();

        if (!isCreator && !isMember) {
            return "You are not part of this group";
        }

        return group;
    }

    private double round(double value) {

        return Math.round(value * 100.0)
                / 100.0;
    }
}