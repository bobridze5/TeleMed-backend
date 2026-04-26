package com.bobridze5.TeleMed_backend.api.mappers;

import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterResponse;
import com.bobridze5.TeleMed_backend.api.dto.auth.RegisterUserRequest;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;

public interface UserAuthMapper extends
        ToEntityMapper<RegisterUserRequest, User>,
        ToResponseMapper<RegisterResponse, User> {

}
