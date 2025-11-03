package com.innowise.userservice.repository;

import com.innowise.userservice.model.CardInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {
    List<CardInfo> findByUserId(Long userId);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM card_info WHERE number = :number)", nativeQuery = true)
    boolean existsByNumber(@Param("number") String number);
}
