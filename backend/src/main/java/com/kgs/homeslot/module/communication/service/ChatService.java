package com.kgs.homeslot.module.communication.service;

import com.kgs.homeslot.module.auth.entity.User;
import com.kgs.homeslot.module.auth.repository.UserRepository;
import com.kgs.homeslot.module.communication.dto.ChatMessageDto;
import com.kgs.homeslot.module.communication.dto.SendChatMessageRequest;
import com.kgs.homeslot.module.communication.entity.ChatMessage;
import com.kgs.homeslot.module.communication.repository.ChatMessageRepository;
import com.kgs.homeslot.module.property.entity.Property;
import com.kgs.homeslot.module.property.repository.PropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatMessageRepository chatRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final NotificationService notificationService;

    public ChatService(ChatMessageRepository chatRepository,
                       UserRepository userRepository,
                       PropertyRepository propertyRepository,
                       NotificationService notificationService) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public ChatMessageDto sendMessage(SendChatMessageRequest req, String senderEmail) {
        User sender = getUserByEmail(senderEmail);
        User recipient = userRepository.findById(req.getRecipientId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found: " + req.getRecipientId()));
        Property property = propertyRepository.findById(req.getPropertyId())
                .orElseThrow(() -> new IllegalArgumentException("Property not found: " + req.getPropertyId()));

        ChatMessage chat = ChatMessage.builder()
                .sender(sender)
                .recipient(recipient)
                .property(property)
                .message(req.getMessage())
                .read(false)
                .build();

        ChatMessage saved = chatRepository.save(chat);

        // Also trigger real-time notification alert for recipient
        notificationService.createNotification(recipient,
                "New Message regarding " + property.getTitle(),
                sender.getEmail().split("@")[0] + ": " + (req.getMessage().length() > 50 ? req.getMessage().substring(0, 50) + "..." : req.getMessage()),
                "MESSAGE_ALERT",
                property.getId());

        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getConversation(Long propertyId, Long otherUserId, String currentUserEmail) {
        User currentUser = getUserByEmail(currentUserEmail);
        List<ChatMessage> conversation = chatRepository.findConversation(propertyId, currentUser.getId(), otherUserId);
        return conversation.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getUserConversations(String currentUserEmail) {
        User currentUser = getUserByEmail(currentUserEmail);
        List<ChatMessage> messages = chatRepository.findAllUserMessages(currentUser.getId());
        return messages.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private ChatMessageDto mapToDto(ChatMessage cm) {
        return ChatMessageDto.builder()
                .id(cm.getId())
                .senderId(cm.getSender().getId())
                .senderEmail(cm.getSender().getEmail())
                .senderName(cm.getSender().getEmail().split("@")[0])
                .recipientId(cm.getRecipient().getId())
                .recipientEmail(cm.getRecipient().getEmail())
                .recipientName(cm.getRecipient().getEmail().split("@")[0])
                .propertyId(cm.getProperty().getId())
                .propertyTitle(cm.getProperty().getTitle())
                .message(cm.getMessage())
                .read(cm.isRead())
                .createdAt(cm.getCreatedAt())
                .build();
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }
}
