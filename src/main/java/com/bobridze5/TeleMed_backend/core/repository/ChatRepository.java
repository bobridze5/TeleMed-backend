package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.medical.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("""
            SELECT DISTINCT c FROM Chat c
            JOIN c.participants p
            WHERE p.id = :userId
            ORDER BY c.createdAt DESC
            """)
    List<Chat> findAllByParticipantId(@Param("userId") Long userId);

    @Query("""
            SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
            FROM Chat c JOIN c.participants p
            WHERE c.id = :chatId AND p.id = :userId
            """)
    boolean existsByIdAndParticipantId(@Param("chatId") Long chatId, @Param("userId") Long userId);

    @Query("""
            SELECT c FROM Chat c
            WHERE SIZE(c.participants) = 2
              AND :userA IN (SELECT p.id FROM c.participants p)
              AND :userB IN (SELECT p.id FROM c.participants p)
            """)
    Optional<Chat> findDirectChat(@Param("userA") Long userA, @Param("userB") Long userB);
}
