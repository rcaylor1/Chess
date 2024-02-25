package service;

import dataAccess.*;
import exception.ResponseException;
import model.*;

import java.util.UUID;

public class UserService {
    private final UserDAO user;
    private final AuthDAO auth;

    public UserService(UserDAO user, AuthDAO auth){
        this.user = user;
        this.auth = auth;
    }

    public AuthData register(UserData userData) throws DataAccessException {
        if (user.getUser(userData.username())!=null){
            throw new DataAccessException("Error: Username already in use");
        } else {
            user.createUser(userData);
            AuthData data = generateAuth(userData.username());
            auth.createAuth(data);
            return data;
        }
    }

    private AuthData generateAuth(String username){
        String authToken = UUID.randomUUID().toString();
        return new AuthData(authToken, username);
    }
}
