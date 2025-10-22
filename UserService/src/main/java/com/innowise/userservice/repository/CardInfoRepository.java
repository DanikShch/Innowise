package com.innowise.userservice.repository;

import com.innowise.userservice.model.CardInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {
    List<CardInfo> findByUserId(Long userId);

    @Query("SELECT c FROM CardInfo c WHERE c.expirationDate < CURRENT_DATE")
    List<CardInfo> findExpiredCards();

    @Query(value = "SELECT * FROM card_info WHERE expiration_date >= CURRENT_DATE", nativeQuery = true)
    List<CardInfo> findNonExpiredCards();
}
