package com.splitxpence.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "settlements")
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private ExpenseGroup group;

    @ManyToOne
    @JoinColumn(name = "paid_from")
    private User paidFrom;

    @ManyToOne
    @JoinColumn(name = "paid_to")
    private User paidTo;

    private double amount;

    private LocalDate settlementDate;

    public Settlement() {
    }

    public Settlement(
            ExpenseGroup group,
            User paidFrom,
            User paidTo,
            double amount,
            LocalDate settlementDate) {

        this.group = group;
        this.paidFrom = paidFrom;
        this.paidTo = paidTo;
        this.amount = amount;
        this.settlementDate = settlementDate;
    }

    public Long getId() {
        return id;
    }

    public ExpenseGroup getGroup() {
        return group;
    }

    public User getPaidFrom() {
        return paidFrom;
    }

    public User getPaidTo() {
        return paidTo;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getSettlementDate() {
        return settlementDate;
    }
}