package com.bank.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.model.Plan;

public interface PlanRepository extends JpaRepository<Plan, Date>{
	Plan findPlanByExecuteState(Integer integer);
}
