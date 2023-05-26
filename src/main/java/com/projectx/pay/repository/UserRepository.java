package com.projectx.pay.repository;

import com.projectx.pay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM Users WHERE msisdn =?1", nativeQuery = true)
    Optional<User>findByMsisdn(String msisdn);

    @Query(value = "SELECT * FROM Users WHERE username =?1", nativeQuery = true)
    Optional<User> findByUsername(String username);
}

