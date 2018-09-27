package com.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.model.ECard;
import com.bank.model.Record;

public interface RecordRepository extends JpaRepository<Record, Long>{
	List<Record> findRecordsByECard(ECard eCard);
}
