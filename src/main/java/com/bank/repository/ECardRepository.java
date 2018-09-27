package com.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.model.ECard;

public interface ECardRepository extends JpaRepository<ECard, String>{
	
}
