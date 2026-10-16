package com.splitxpence.controller;

import com.splitxpence.model.Expense;
import com.splitxpence.model.ExpenseGroup;
import com.splitxpence.model.ExpenseSplit;
import com.splitxpence.model.Settlement;
import com.splitxpence.model.User;

import com.splitxpence.repository.ExpenseRepository;
import com.splitxpence.repository.ExpenseSplitRepository;
import com.splitxpence.repository.GroupMemberRepository;
import com.splitxpence.repository.GroupRepository;
import com.splitxpence.repository.SettlementRepository;
import com.splitxpence.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.bind.annotation.*;

@RestController
public class BalanceController {

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final SettlementRepository settlementRepository;

    public BalanceController(
            ExpenseRepository expenseRepository,
            ExpenseSplitRepository expenseSplitRepository,
            GroupRepository groupRepository,
            GroupMemberRepository groupMemberRepository,
            UserRepository userRepository,
            SettlementRepository settlementRepository) {

        this.expenseRepository = expenseRepository;
        this.expenseSplitRepository = expenseSplitRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
        this.settlementRepository = settlementRepository;
    }

    @GetMapping("/group-balance")
    public Object groupBalance(
            @RequestParam Long groupId,
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

        ExpenseGroup group =
                groupOptional.get();

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
            return "You are not part of this group";
        }

        Map<Long, Double> balances =
                calculateBalances(groupId);

        Map<String, Double> result =
                new HashMap<>();

        for (Map.Entry<Long, Double> entry
                : balances.entrySet()) {

            Optional<User> userOptional =
                    userRepository.findById(
                            entry.getKey());

            if (userOptional.isPresent()) {

                result.put(
                        userOptional.get().getName(),
                        round(entry.getValue())
                );
            }
        }

        return result;
    }

    @GetMapping("/my-balance")
    public Object myBalance(
            @RequestParam Long groupId,
            HttpSession session) {

        Long loggedInUserId =
                (Long) session.getAttribute("userId");

        if (loggedInUserId == null) {
            return "Please login first";
        }

        Map<Long, Double> balances =
                calculateBalances(groupId);

        double balance =
                round(
                        balances.getOrDefault(
                                loggedInUserId,
                                0.0
                        )
                );

        Optional<User> userOptional =
                userRepository.findById(
                        loggedInUserId);

        if (userOptional.isEmpty()) {
            return "User not found";
        }

        String name =
                userOptional.get().getName();

        if (balance > 0) {

            return name
                    + " should receive ₹"
                    + balance;
        }

        if (balance < 0) {

            return name
                    + " owes ₹"
                    + Math.abs(balance);
        }

        return name
                + " has no pending balance";
    }

    @GetMapping("/who-owes-whom")
    public Object whoOwesWhom(
            @RequestParam Long groupId,
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

        Map<Long, Double> balances =
                calculateBalances(groupId);

        List<PersonBalance> creditors =
                new ArrayList<>();

        List<PersonBalance> debtors =
                new ArrayList<>();

        for (Map.Entry<Long, Double> entry
                : balances.entrySet()) {

            double balance =
                    round(entry.getValue());

            if (balance > 0.01) {

                creditors.add(
                        new PersonBalance(
                                entry.getKey(),
                                balance
                        )
                );

            } else if (balance < -0.01) {

                debtors.add(
                        new PersonBalance(
                                entry.getKey(),
                                Math.abs(balance)
                        )
                );
            }
        }

        List<String> result =
                new ArrayList<>();

        int debtorIndex = 0;
        int creditorIndex = 0;

        while (debtorIndex < debtors.size()
                && creditorIndex < creditors.size()) {

            PersonBalance debtor =
                    debtors.get(debtorIndex);

            PersonBalance creditor =
                    creditors.get(creditorIndex);

            double payment =
                    Math.min(
                            debtor.amount,
                            creditor.amount
                    );

            Optional<User> debtorUser =
                    userRepository.findById(
                            debtor.userId);

            Optional<User> creditorUser =
                    userRepository.findById(
                            creditor.userId);

            if (debtorUser.isPresent()
                    && creditorUser.isPresent()) {

                result.add(
                        debtorUser.get().getName()
                        + " owes "
                        + creditorUser.get().getName()
                        + " ₹"
                        + round(payment)
                );
            }

            debtor.amount -= payment;
            creditor.amount -= payment;

            if (debtor.amount < 0.01) {
                debtorIndex++;
            }

            if (creditor.amount < 0.01) {
                creditorIndex++;
            }
        }

        if (result.isEmpty()) {
            result.add("All balances are settled");
        }

        return result;
    }

    private Map<Long, Double> calculateBalances(
            Long groupId) {

        Map<Long, Double> balances =
                new HashMap<>();

        List<Expense> expenses =
                expenseRepository
                        .findByGroupId(groupId);

        for (Expense expense : expenses) {

            Long payerId =
                    expense.getPaidBy().getId();

            balances.put(
                    payerId,
                    balances.getOrDefault(
                            payerId,
                            0.0
                    ) + expense.getAmount()
            );

            List<ExpenseSplit> splits =
                    expenseSplitRepository
                            .findByExpenseId(
                                    expense.getId());

            for (ExpenseSplit split : splits) {

                Long userId =
                        split.getUser().getId();

                balances.put(
                        userId,
                        balances.getOrDefault(
                                userId,
                                0.0
                        ) - split.getShareAmount()
                );
            }
        }

        List<Settlement> settlements =
                settlementRepository
                        .findByGroupId(groupId);

        for (Settlement settlement : settlements) {

            Long fromUserId =
                    settlement.getPaidFrom().getId();

            Long toUserId =
                    settlement.getPaidTo().getId();

            double amount =
                    settlement.getAmount();

            balances.put(
                    fromUserId,
                    balances.getOrDefault(
                            fromUserId,
                            0.0
                    ) + amount
            );

            balances.put(
                    toUserId,
                    balances.getOrDefault(
                            toUserId,
                            0.0
                    ) - amount
            );
        }

        return balances;
    }

    private double round(double value) {

        return Math.round(value * 100.0)
                / 100.0;
    }

    private static class PersonBalance {

        Long userId;
        double amount;

        PersonBalance(
                Long userId,
                double amount) {

            this.userId = userId;
            this.amount = amount;
        }
    }
}