package com.leadlucky.api.util

class PasswordUtil {

    static final String SPECIAL_CHARS = ' !@#$%^&*()_-+=\\|/?.,<>`~[]{}\'";:'
    static final String NUMBER_CHARS = '01234567890'

    static final int MIN_USERNAME = 5
    static final int MAX_USERNAME = 25

    static final int MIN_PASSWORD = 8
    static final int MAX_PASSWORD = 48

    static boolean isUsernameValid(String username) {
        return username && username.length() >= MIN_USERNAME                \
                && username.length() <= MAX_USERNAME                        \
                && SPECIAL_CHARS.findIndexOf { username.contains(it) } < 0
    }

    static boolean isPasswordValid(String password) {
        return password && password.length() >= MIN_PASSWORD                 \
                && password.length() <= MAX_PASSWORD                         \
                && password.toUpperCase() != password                        \
                && password.toLowerCase() != password                        \
                && SPECIAL_CHARS.findIndexOf { password.contains(it) } > -1  \
                && NUMBER_CHARS.findIndexOf { password.contains(it) } > -1
    }

}