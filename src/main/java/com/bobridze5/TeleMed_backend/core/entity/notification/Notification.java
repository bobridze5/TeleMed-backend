package com.bobridze5.TeleMed_backend.core.entity.notification;

import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "notification")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @Column(name = "notification_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reminder_id", referencedColumnName = "reminder_id")
    private Reminder reminder;

    @Column(name = "notification_title", nullable = false, length = 200)
    private String title;

    @Column(name = "notification_message", length = 1000)
    private String message;

    @Column(name = "notification_type", length = 40)
    private String type;

    @Column(name = "notification_is_read", nullable = false)
    private Boolean isRead;

    @CreationTimestamp
    @Column(name = "notification_created_at")
    private Instant createdAt;
}
