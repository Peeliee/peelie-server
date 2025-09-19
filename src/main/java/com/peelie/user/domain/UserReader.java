package com.peelie.user.domain;

public interface UserReader {
    User getUser(String userToken);
}
