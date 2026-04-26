package com.bobridze5.TeleMed_backend.core.service.chat;

import com.bobridze5.TeleMed_backend.api.dto.chat.*;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.medical.Chat;
import com.bobridze5.TeleMed_backend.core.entity.medical.ChatMessage;
import com.bobridze5.TeleMed_backend.core.exceptions.AccessForbiddenException;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.ChatMessageRepository;
import com.bobridze5.TeleMed_backend.core.repository.ChatRepository;
import com.bobridze5.TeleMed_backend.core.repository.UserRepository;
import com.bobridze5.TeleMed_backend.core.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${app.upload.chat-dir:uploads/chat-attachments}")
    private String chatUploadDir;

    @Transactional(readOnly = true)
    public List<ChatResponse> getChatsForUser(User user) {
        return chatRepository.findAllByParticipantId(user.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ChatResponse createDirectChat(User currentUser, ChatCreateRequest request) {
        Long otherUserId = request.userId();

        if (otherUserId.equals(currentUser.getId())) {
            throw new AccessForbiddenException("Нельзя создать чат с самим собой");
        }

        User other = userRepository.findById(otherUserId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        return chatRepository.findDirectChat(currentUser.getId(), other.getId())
                .map(this::toResponse)
                .orElseGet(() -> {
                    Set<User> participants = new HashSet<>();
                    participants.add(currentUser);
                    participants.add(other);
                    Chat chat = Chat.builder()
                            .participants(participants)
                            .build();

                    return toResponse(chatRepository.save(chat));
                });
    }

    @Transactional(readOnly = true)
    public Page<MessageResponse> getMessages(Long chatId, User user, int page, int size) {
        assertParticipant(chatId, user);
        return chatMessageRepository.findByChatIdOrderBySentAtAsc(chatId, PageRequest.of(page, size))
                .map(this::toMessageResponse);
    }

    @Transactional
    public MessageResponse sendMessage(Long chatId, User sender, MessageRequest request) {
        if (request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("Сообщение не может быть пустым");
        }
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Чат не найден"));
        assertParticipant(chatId, sender);

        ChatMessage saved = chatMessageRepository.save(ChatMessage.builder()
                .chat(chat).sender(sender).content(request.content()).build());

        MessageResponse response = toMessageResponse(saved);
        broadcast(chatId, "NEW", response);
        return response;
    }

    @Transactional
    public MessageResponse sendAttachment(Long chatId, User sender, MultipartFile file, String caption) throws IOException {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Чат не найден"));
        assertParticipant(chatId, sender);

        String originalName = file.getOriginalFilename();
        String ext = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf('.'))
                : "";
        String stored = UUID.randomUUID() + ext;

        Path dir = Paths.get(chatUploadDir);
        Files.createDirectories(dir);
        file.transferTo(dir.resolve(stored).toAbsolutePath());

        ChatMessage saved = chatMessageRepository.save(ChatMessage.builder()
                .chat(chat)
                .sender(sender)
                .content(caption != null && !caption.isBlank() ? caption : null)
                .attachmentName(originalName)
                .attachmentMime(file.getContentType())
                .attachmentSize(file.getSize())
                .attachmentStoredAs(stored)
                .build());

        MessageResponse response = toMessageResponse(saved);
        broadcast(chatId, "NEW", response);
        return response;
    }

    @Transactional(readOnly = true)
    public AttachmentData getAttachment(Long chatId, Long messageId, User user) throws IOException {
        assertParticipant(chatId, user);
        ChatMessage msg = chatMessageRepository.findByIdAndChatId(messageId, chatId)
                .orElseThrow(() -> new EntityNotFoundException("Сообщение не найдено"));
        if (msg.getAttachmentStoredAs() == null) {
            throw new EntityNotFoundException("Вложение не найдено");
        }
        Path path = Paths.get(chatUploadDir).resolve(msg.getAttachmentStoredAs());
        if (!Files.exists(path)) {
            throw new EntityNotFoundException("Файл вложения не найден");
        }
        return new AttachmentData(Files.readAllBytes(path), msg.getAttachmentName(), msg.getAttachmentMime());
    }

    @Transactional
    public MessageResponse editMessage(Long chatId, Long messageId, User user, MessageRequest request) {
        ChatMessage msg = chatMessageRepository.findByIdAndChatId(messageId, chatId)
                .orElseThrow(() -> new EntityNotFoundException("Сообщение не найдено"));
        if (!msg.getSender().getId().equals(user.getId())) {
            throw new AccessForbiddenException("Нельзя редактировать чужое сообщение");
        }
        msg.setContent(request.content());
        msg.setEditedAt(LocalDateTime.now());

        MessageResponse response = toMessageResponse(msg);
        broadcast(chatId, "EDITED", response);
        return response;
    }

    @Transactional
    public void deleteMessage(Long chatId, Long messageId, User user) {
        ChatMessage msg = chatMessageRepository.findByIdAndChatId(messageId, chatId)
                .orElseThrow(() -> new EntityNotFoundException("Сообщение не найдено"));
        if (!msg.getSender().getId().equals(user.getId())) {
            throw new AccessForbiddenException("Нельзя удалить чужое сообщение");
        }
        if (msg.getAttachmentStoredAs() != null) {
            try {
                Files.deleteIfExists(Paths.get(chatUploadDir).resolve(msg.getAttachmentStoredAs()));
            } catch (IOException ignored) {
            }
        }
        chatMessageRepository.delete(msg);
        broadcast(chatId, "DELETED", new MessageResponse(
                messageId, chatId, null, null, null, null, null, null, null, null, null));
    }

    public void sendMessageWs(Long chatId, Principal principal, MessageRequest request) {
        sendMessage(chatId, extractUser(principal), request);
    }

    private void assertParticipant(Long chatId, User user) {
        if (!chatRepository.existsByIdAndParticipantId(chatId, user.getId())) {
            throw new AccessForbiddenException("Нет доступа к этому чату");
        }
    }

    private void broadcast(Long chatId, String type, MessageResponse message) {
        messagingTemplate.convertAndSend("/topic/chat/" + chatId, new ChatEvent(type, message));
    }

    private User extractUser(Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken auth
                && auth.getPrincipal() instanceof UserDetailsImpl ud) {
            return ud.getUser();
        }
        throw new AccessForbiddenException("Не авторизован");
    }

    private ChatResponse toResponse(Chat c) {
        List<ChatParticipantDto> participants = c.getParticipants().stream()
                .map(u -> new ChatParticipantDto(u.getId(), u.getFullName(), u.getRole()))
                .toList();
        return new ChatResponse(c.getId(), participants, c.getCreatedAt());
    }

    private MessageResponse toMessageResponse(ChatMessage m) {
        String url = m.getAttachmentStoredAs() != null
                ? "/api/v1/chats/" + m.getChat().getId() + "/messages/" + m.getId() + "/attachment"
                : null;
        return new MessageResponse(
                m.getId(),
                m.getChat().getId(),
                m.getSender().getId(),
                m.getSender().getFullName(),
                m.getContent(),
                m.getAttachmentName(),
                m.getAttachmentMime(),
                m.getAttachmentSize(),
                url,
                m.getSentAt(),
                m.getEditedAt()
        );
    }

    public record AttachmentData(byte[] bytes, String filename, String mime) {}
}
