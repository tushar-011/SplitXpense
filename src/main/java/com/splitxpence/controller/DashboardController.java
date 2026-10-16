package com.splitxpence.controller;

import com.splitxpence.model.Expense;
import com.splitxpence.model.ExpenseGroup;
import com.splitxpence.model.ExpenseSplit;
import com.splitxpence.model.GroupMember;
import com.splitxpence.model.Settlement;

import com.splitxpence.repository.ExpenseRepository;
import com.splitxpence.repository.ExpenseSplitRepository;
import com.splitxpence.repository.GroupMemberRepository;
import com.splitxpence.repository.GroupRepository;
import com.splitxpence.repository.SettlementRepository;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final SettlementRepository settlementRepository;

    public DashboardController(
            GroupRepository groupRepository,
            GroupMemberRepository groupMemberRepository,
            ExpenseRepository expenseRepository,
            ExpenseSplitRepository expenseSplitRepository,
            SettlementRepository settlementRepository) {

        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.expenseRepository = expenseRepository;
        this.expenseSplitRepository = expenseSplitRepository;
        this.settlementRepository = settlementRepository;
    }

    @GetMapping("/dashboard-data")
    public Object getDashboard(HttpSession session) {

        Long userId =
                (Long) session.getAttribute("userId");

        if (userId == null) {
            return "Please login first";
        }

        Set<Long> groupIds = new HashSet<>();

        // Groups created by the logged-in user
        List<ExpenseGroup> createdGroups =
                groupRepository.findByCreatedById(userId);

        for (ExpenseGroup group : createdGroups) {
            groupIds.add(group.getId());
        }

        // Groups where the user is a member
        List<GroupMember> memberships =
                groupMemberRepository.findAll();

        for (GroupMember membership : memberships) {

            if (membership.getUser()
                    .getId()
                    .equals(userId)) {

                groupIds.add(
                        membership.getGroup().getId()
                );
            }
        }

        double totalExpenses = 0.0;

        Map<Long, Double> balances =
                new HashMap<>();

        List<Map<String, Object>> recentExpenses =
                new ArrayList<>();

        for (Long groupId : groupIds) {

            List<Expense> expenses =
                    expenseRepository.findByGroupId(groupId);

            for (Expense expense : expenses) {

                totalExpenses += expense.getAmount();

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
                                        expense.getId()
                                );

                for (ExpenseSplit split : splits) {

                    Long participantId =
                            split.getUser().getId();

                    balances.put(
                            participantId,
                            balances.getOrDefault(
                                    participantId,
                                    0.0
                            ) - split.getShareAmount()
                    );
                }

                Map<String, Object> recent =
                        new HashMap<>();

                recent.put(
                        "id",
                        expense.getId()
                );

                recent.put(
                        "title",
                        expense.getTitle()
                );

                recent.put(
                        "amount",
                        expense.getAmount()
                );

                recent.put(
                        "category",
                        expense.getCategory()
                );

                recent.put(
                        "date",
                        expense.getExpenseDate()
                );

                recent.put(
                        "group",
                        expense.getGroup().getName()
                );

                recent.put(
                        "paidBy",
                        expense.getPaidBy().getName()
                );

                recentExpenses.add(recent);
            }

            List<Settlement> settlements =
                    settlementRepository
                            .findByGroupId(groupId);

            for (Settlement settlement : settlements) {

                Long fromId =
                        settlement.getPaidFrom().getId();

                Long toId =
                        settlement.getPaidTo().getId();

                double amount =
                        settlement.getAmount();

                balances.put(
                        fromId,
                        balances.getOrDefault(
                                fromId,
                                0.0
                        ) + amount
                );

                balances.put(
                        toId,
                        balances.getOrDefault(
                                toId,
                                0.0
                        ) - amount
                );
            }
        }

        double myBalance =
                balances.getOrDefault(
                        userId,
                        0.0
                );

        double amountYouOwe = 0.0;
        double amountOwedToYou = 0.0;

        if (myBalance < 0) {

            amountYouOwe =
                    Math.abs(myBalance);

        } else if (myBalance > 0) {

            amountOwedToYou =
                    myBalance;
        }

        // Show maximum 5 latest entries
        recentExpenses.sort(
                (a, b) ->
                        b.get("date")
                                .toString()
                                .compareTo(
                                        a.get("date")
                                                .toString()
                                )
        );

        if (recentExpenses.size() > 5) {

            recentExpenses =
                    new ArrayList<>(
                            recentExpenses.subList(
                                    0,
                                    5
                            )
                    );
        }

        Map<String, Object> dashboard =
                new HashMap<>();

        dashboard.put(
                "userName",
                session.getAttribute("userName")
        );

        dashboard.put(
                "totalGroups",
                groupIds.size()
        );

        dashboard.put(
                "totalExpenses",
                round(totalExpenses)
        );

        dashboard.put(
                "amountYouOwe",
                round(amountYouOwe)
        );

        dashboard.put(
                "amountOwedToYou",
                round(amountOwedToYou)
        );

        dashboard.put(
                "recentExpenses",
                recentExpenses
        );

        return dashboard;
    }

    private double round(double value) {

        return Math.round(value * 100.0)
                / 100.0;
    }
}