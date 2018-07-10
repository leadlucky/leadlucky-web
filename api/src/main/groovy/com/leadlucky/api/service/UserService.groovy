package com.leadlucky.api.service

import com.leadlucky.api.model.User

interface UserService {

    void createUser(User user)

    void updateUser(String username, User user)

    void deleteUser(String username)

    User getUser(String username)

    List<User> getUsers()

    List<User> getUsers(int page, int length)
}