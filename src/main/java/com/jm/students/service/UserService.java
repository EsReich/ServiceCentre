package com.jm.students.service;

import com.jm.students.model.User;

public interface UserService extends AbstractEntityService<User> {
    public User getUserByTelegramId(String telegramChatId);
}
