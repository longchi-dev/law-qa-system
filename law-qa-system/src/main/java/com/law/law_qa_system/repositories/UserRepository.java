package com.law.law_qa_system.repositories;

import com.law.law_qa_system.models.Account;
import com.law.law_qa_system.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByName(String name);

    Optional<User> findByAccountId(Long accountId);

    Optional<User> findByPhoneNum(String phoneNum);
    Optional<User> findByAccount(Account account);
}
