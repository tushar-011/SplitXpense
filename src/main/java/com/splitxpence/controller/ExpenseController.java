package com.splitxpence.controller;

import com.splitxpence.model.Expense;
import com.splitxpence.model.ExpenseGroup;
import com.splitxpence.model.User;

import com.splitxpence.repository.ExpenseRepository;
import com.splitxpence.repository.GroupMemberRepository;
import com.splitxpence.repository.GroupRepository;
import com.splitxpence.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.*;

@RestController
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    public ExpenseController(
            ExpenseRepository expenseRepository,
            GroupRepository groupRepository,
            UserRepository userRepository,
            GroupMemberRepository groupMemberRepository) {

        this.expenseRepository = expenseRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    @PostMapping("/add-expense")
    public String addExpense(
            @RequestParam Long groupId,
            @RequestParam String title,
            @RequestParam double amount,
            @RequestParam String expenseDate,
            @RequestParam String category,
            @RequestParam String payerEmail,
            HttpSession session) {

        Long loggedInUserId =
                (Long) session.getAttribute("userId");

        if (loggedInUserId == null) {
            return "Please login first";
        }

        Optional<ExpenseGroup> groupOptional =
                groupRepository.findById(groupId);

        if (groupOptional.isEmpty()) {
            return "Group not found";
        }

        ExpenseGroup group = groupOptional.get();

        boolean isCreator =
                group.getCreatedBy().getId().equals(loggedInUserId);

        boolean isMember =
                groupMemberRepository
                        .findByGroupIdAndUserId(
                                groupId,
                                loggedInUserId
                        )
                        .isPresent();

        if (!isCreator && !isMember) {
            return "You are not a member of this group";
        }

        Optional<User> payerOptional =
                userRepository.findByEmail(payerEmail);

        if (payerOptional.isEmpty()) {
            return "Payer not found";
        }

        User payer = payerOptional.get();

        boolean payerIsCreator =
                group.getCreatedBy().getId().equals(payer.getId());

        boolean payerIsMember =
                groupMemberRepository
                        .findByGroupIdAndUserId(
                                groupId,
                                payer.getId()
                        )
                        .isPresent();

        if (!payerIsCreator && !payerIsMember) {
            return "Payer is not part of this group";
        }

        Expense expense = new Expense(
                title,
                amount,
                LocalDate.parse(expenseDate),
                category,
                group,
                payer
        );

        expenseRepository.save(expense);

        return "Expense added successfully";
    }

    @GetMapping("/expenses")
    public List<Expense> viewExpenses(
            @RequestParam Long groupId,
            HttpSession session) {

        Long loggedInUserId =
                (Long) session.getAttribute("userId");

        if (loggedInUserId == null) {
            return List.of();
        }

        return expenseRepository.findByGroupId(groupId);
    }

    @PostMapping("/edit-expense")
    public String editExpense(
            @RequestParam Long expenseId,
            @RequestParam String title,
            @RequestParam double amount,
            @RequestParam String expenseDate,
            @RequestParam String category,
            HttpSession session) {

        Long loggedInUserId =
                (Long) session.getAttribute("userId");

        if (loggedInUserId == null) {
            return "Please login first";
        }

        Optional<Expense> expenseOptional =
                expenseRepository.findById(expenseId);

        if (expenseOptional.isEmpty()) {
            return "Expense not found";
        }

        Expense expense = expenseOptional.get();

        expense.setTitle(title);
        expense.setAmount(amount);
        expense.setExpenseDate(
                LocalDate.parse(expenseDate)
        );
        expense.setCategory(category);

        expenseRepository.save(expense);

        return "Expense updated successfully";
    }

    @PostMapping("/delete-expense")
    public String deleteExpense(
            @RequestParam Long expenseId,
            HttpSession session) {

        Long loggedInUserId =
                (Long) session.getAttribute("userId");

        if (loggedInUserId == null) {
            return "Please login first";
        }

        Optional<Expense> expenseOptional =
                expenseRepository.findById(expenseId);

        if (expenseOptional.isEmpty()) {
            return "Expense not found";
        }

        expenseRepository.deleteById(expenseId);

        return "Expense deleted successfully";
    }
}