package com.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.bank.model.Loan;
import com.bank.model.User;

public interface LoanRepository extends JpaRepository<Loan, Long>{
	/**
	 * 通过用户查找他的贷款
	 * @param user 用户
	 * @return 返回该用户的贷款
	 */
	Loan findLoanByUser(User user);
	
	@Transactional
	int deleteLoanById(Long id);
}
