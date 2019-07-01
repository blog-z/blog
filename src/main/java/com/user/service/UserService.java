package com.user.service;

import com.user.commons.ServerResponse;
import com.user.entity.User;

public interface UserService {
    ServerResponse<String> register(User user);

    ServerResponse login(String userName, String userEmail, String password);

    ServerResponse getQuestion(String userName, String userPassword);

    ServerResponse setAnswer(String userName, String userEmail, String answer);

    ServerResponse setPassword(String userName, String userEmail, String password);

    ServerResponse getUserMessage(String userName, String userEmail);
}
