package ru.traintickets.businesslogic.api;

import ru.traintickets.businesslogic.transport.LoginForm;
import ru.traintickets.businesslogic.transport.RegisterForm;
import ru.traintickets.businesslogic.transport.UserInfo;

import java.util.UUID;

public interface AuthService {
    void register(RegisterForm form);
    void login(LoginForm form);
    void logout(UUID id);
    UserInfo getUserInfo(UUID id);
}
