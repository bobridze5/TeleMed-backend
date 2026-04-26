CREATE TABLE chats (
    chat_id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE chat_participants (
    chat_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,

    CONSTRAINT pk_chat_participants PRIMARY KEY (chat_id, user_id),
    CONSTRAINT fk_chat_participants_chat FOREIGN KEY (chat_id) REFERENCES chats(chat_id) ON DELETE CASCADE,
    CONSTRAINT fk_chat_participants_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX idx_chat_participants_user_id ON chat_participants(user_id);

CREATE TABLE chat_messages (
    message_id BIGSERIAL PRIMARY KEY,
    chat_id    BIGINT NOT NULL,
    sender_id  BIGINT NOT NULL,
    content    TEXT,
    attachment_name      VARCHAR(255),
    attachment_mime      VARCHAR(100),
    attachment_size      BIGINT,
    attachment_stored_as VARCHAR(255),
    sent_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    edited_at  TIMESTAMP WITHOUT TIME ZONE,

    CONSTRAINT fk_message_chat   FOREIGN KEY (chat_id)   REFERENCES chats(chat_id) ON DELETE CASCADE,
    CONSTRAINT fk_message_sender FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT chat_message_has_content_or_attachment CHECK (content IS NOT NULL OR attachment_stored_as IS NOT NULL)
);

CREATE INDEX idx_chat_messages_chat_id ON chat_messages(chat_id);
