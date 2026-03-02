package com.bobridze5.TeleMed_backend.core.event;

import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OnRegistrationCompleteEvent {
    private User user;
    private String url;
}
