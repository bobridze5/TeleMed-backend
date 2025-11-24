package com.devops_labs.userService.core.event;

import com.devops_labs.userService.core.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OnRegistrationCompleteEvent {
    private User user;
    private String url;
}
