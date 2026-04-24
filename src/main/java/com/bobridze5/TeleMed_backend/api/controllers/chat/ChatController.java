package com.bobridze5.TeleMed_backend.api.controllers.chat;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.chat.*;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentUser;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.service.chat.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping(API.CHATS)
@RequiredArgsConstructor
@Tag(name = "Чаты")
public class ChatController {
    private final ChatService chatService;

    @GetMapping
    @Operation(summary = "Список чатов пользователя")
    public List<ChatResponse> getChats(@CurrentUser User user) {
        return chatService.getChatsForUser(user);
    }

    @PostMapping
    @Operation(summary = "Создать (или получить существующий) чат с другим пользователем")
    public ResponseEntity<ChatResponse> createChat(
            @CurrentUser User user,
            @Valid @RequestBody ChatCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chatService.createDirectChat(user, request));
    }

    @GetMapping("/{chatId}/messages")
    @Operation(summary = "Сообщения чата")
    public Page<MessageResponse> getMessages(
            @CurrentUser User user,
            @PathVariable Long chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return chatService.getMessages(chatId, user, page, size);
    }

    @PostMapping("/{chatId}/messages")
    @Operation(summary = "Отправить сообщение")
    public ResponseEntity<MessageResponse> sendMessage(
            @CurrentUser User user,
            @PathVariable Long chatId,
            @Valid @RequestBody MessageRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatService.sendMessage(chatId, user, request));
    }

    @PostMapping(value = "/{chatId}/messages/attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Отправить файл как сообщение")
    public ResponseEntity<MessageResponse> sendAttachment(
            @CurrentUser User user,
            @PathVariable Long chatId,
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "caption", required = false) String caption
    ) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatService.sendAttachment(chatId, user, file, caption));
    }

    @GetMapping("/{chatId}/messages/{messageId}/attachment")
    @Operation(summary = "Скачать вложение сообщения")
    public ResponseEntity<byte[]> getAttachment(
            @CurrentUser User user,
            @PathVariable Long chatId,
            @PathVariable Long messageId
    ) throws IOException {
        ChatService.AttachmentData data = chatService.getAttachment(chatId, messageId, user);
        ContentDisposition disposition = ContentDisposition.inline()
                .filename(data.filename() != null ? data.filename() : "attachment", StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(data.mime() != null
                        ? MediaType.parseMediaType(data.mime())
                        : MediaType.APPLICATION_OCTET_STREAM)
                .body(data.bytes());
    }

    @PatchMapping("/{chatId}/messages/{messageId}")
    @Operation(summary = "Редактировать сообщение")
    public MessageResponse editMessage(
            @CurrentUser User user,
            @PathVariable Long chatId,
            @PathVariable Long messageId,
            @Valid @RequestBody MessageRequest request
    ) {
        return chatService.editMessage(chatId, messageId, user, request);
    }

    @DeleteMapping("/{chatId}/messages/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить сообщение")
    public void deleteMessage(
            @CurrentUser User user,
            @PathVariable Long chatId,
            @PathVariable Long messageId
    ) {
        chatService.deleteMessage(chatId, messageId, user);
    }
}
