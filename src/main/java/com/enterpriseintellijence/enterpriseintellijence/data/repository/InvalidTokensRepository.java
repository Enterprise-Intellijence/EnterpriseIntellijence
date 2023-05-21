package com.enterpriseintellijence.enterpriseintellijence.data.repository;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.InvalidToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvalidTokensRepository extends JpaRepository<InvalidToken,String>, JpaSpecificationExecutor<InvalidToken> {

    InvalidToken findByToken(String token);

    List<InvalidToken> findAllByExpirationDateBefore(LocalDateTime expirationDate);

}
