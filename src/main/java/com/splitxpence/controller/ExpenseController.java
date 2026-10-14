package com.splitxpence.controller;

import com.splitxpence.model.Expense;
import com.splitxpence.model.ExpenseGroup;
import com.splitxpence.model.ExpenseSplit;
import com.splitxpence.model.User;

import com.splitxpence.repository.ExpenseRepository;
import com.splitxpence.repository.ExpenseSplitRepository;
import com.splitxpence.repository.GroupMemberRepository;
import com.splitxpence.repository.GroupRepository;
import com.splitxpence.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.web.bind.annotation.*;

@RestController
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    public ExpenseController(
            ExpenseRepository expenseRepository,
            ExpenseSplitRepository expenseSplitRepository,
            GroupRepository groupRepository,
            UserRepository userRepository,
            GroupMemberRepository groupMemberRepository) {

        this.expenseRepository = expenseRepository;
        this.expenseSplitRepository = expenseSplitRepository;
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
            @RequestParam String participantEmails,
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
                group.getCreatedBy()
                        .getId()
                        .equals(loggedInUserId);

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
                group.getCreatedBy()
                        .getId()
                        .equals(payer.getId());

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

        String[] emails =
                participantEmails.split(",");

        List<User> participants =
                new ArrayList<>();

        Set<Long> addedUserIds =
                new HashSet<>();

        for (String email : emails) {

            email = email.trim();

            Optional<User> userOptional =
                    userRepository.findByEmail(email);

            if (userOptional.isEmpty()) {
                return "Participant not found: " + email;
            }

            User participant =
                    userOptional.get();

            boolean participantIsCreator =
                    group.getCreatedBy()
                            .getId()
                            .equals(participant.getId());

            boolean participantIsMember =
                    groupMemberRepository
                            .findByGroupIdAndUserId(
                                    groupId,
                                    participant.getId()
                            )
                            .isPresent();

            if (!participantIsCreator
                    && !participantIsMember) {

                return email
                        + " is not part of this group";
            }

            if (!addedUserIds.contains(
                    participant.getId())) {

                participants.add(participant);

                addedUserIds.add(
                        participant.getId());
            }
        }

        if (participants.isEmpty()) {
            return "Please select at least one participant";
        }

        Expense expense =
                new Expense(
                        title,
                        amount,
                        LocalDate.parse(expenseDate),
                        category,
                        group,
                        payer
                );

        expenseRepository.save(expense);

        double shareAmount =
                amount / participants.size();

        for (User participant : participants) {

            ExpenseSplit split =
                    new ExpenseSplit(
                            expense,
                            participant,
                            shareAmount
                    );

            expenseSplitRepository.save(split);
        }

        return "Expense added successfully. "
                + "Each participant owes: "
                + String.format("%.2f", shareAmount);
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

        return expenseRepository
                .findByGroupId(groupId);
    }

    @GetMapping("/expense-splits")
    public List<ExpenseSplit> viewExpenseSplits(
            @RequestParam Long expenseId,
            HttpSession session) {

        Long loggedInUserId =
                (Long) session.getAttribute("userId");

        if (loggedInUserId == null) {
            return List.of();
        }

        return expenseSplitRepository
                .findByExpenseId(expenseId);
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

        Expense expense =
                expenseOptional.get();

        expense.setTitle(title);
        expense.setAmount(amount);
        expense.setExpenseDate(
                LocalDate.parse(expenseDate)
        );
        expense.setCategory(category);

        expenseRepository.save(expense);

        List<ExpenseSplit> splits =
                expenseSplitRepository
                        .findByExpenseId(expenseId);

        if (!splits.isEmpty()) {

            double newShare =
                    amount / splits.size();

            for (ExpenseSplit split : splits) {

                split.setShareAmount(newShare);

                expenseSplitRepository.save(split);
            }
        }

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

        List<ExpenseSplit> splits =
                expenseSplitRepository
                        .findByExpenseId(expenseId);

        expenseSplitRepository.deleteAll(splits);

        expenseRepository.deleteById(expenseId);

        return "Expense deleted successfully";
    }
}