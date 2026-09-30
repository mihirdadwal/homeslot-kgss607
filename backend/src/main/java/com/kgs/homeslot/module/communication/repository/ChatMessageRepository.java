package com.kgs.homeslot.module.communication.repository;

import com.kgs.homeslot.module.communication.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.property.id = :propertyId " +
           "AND ((cm.sender.id = :user1Id AND cm.recipient.id = :user2Id) " +
           "OR (cm.sender.id = :user2Id AND cm.recipient.id = :user1Id)) " +
           "ORDER BY cm.createdAt ASC")
    List<ChatMessage> findConversation(@Param("propertyId") Long propertyId,
                                      @Param("user1Id") Long user1Id,
                                      @Param("user2Id") Long user2Id);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.sender.id = :userId OR cm.recipient.id = :userId ORDER BY cm.createdAt DESC")
    List<ChatMessage> findAllUserMessages(@Param("userId") Long userId);
}
