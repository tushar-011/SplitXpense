package com.splitxpence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<ExpenseGroup, Long> {

    List<ExpenseGroup> findByCreatedById(Long userId);
}