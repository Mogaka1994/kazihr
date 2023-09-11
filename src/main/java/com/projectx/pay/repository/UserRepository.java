package com.projectx.pay.repository;

import com.projectx.pay.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM Users WHERE msisdn =?1", nativeQuery = true)
    Optional<User>findByMsisdn(String msisdn);

    @Query(value = "SELECT * FROM Users WHERE username =?1", nativeQuery = true)
    Optional<User> findByUsername(String username);

    @Query("FROM User e")
    List<User> getAllUsers();

//    List<User> findAllByName(String name, Pageable pageable);
}

