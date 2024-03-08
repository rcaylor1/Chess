package service;

import dataAccess.*;
import dataAccess.Exceptions.*;
import model.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
            throw new AlreadyTakenException("Error: already taken");
        } if (userData.username() == null || userData.password() == null || userData.email() == null){
            throw new BadRequestException("Error: bad request");
        }
        else {
            user.createUser(userData);
            AuthData data = generateAuth(userData.username());
            auth.createAuth(data);
            return data;
        }
    }

    public AuthData login(LoginRequest loginRequest) throws DataAccessException{
        UserData newUser = user.getUser(loginRequest.username());
        if (newUser == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//
//        if (!encoder.matches(newUser.password(), loginRequest.password())) throw new DataAccessException("Error: incorrect password");
        if (!newUser.password().equals(loginRequest.password())){
            throw new DataAccessException("Error: unauthorized");
        }
        else {
            AuthData data = generateAuth(loginRequest.username());
            auth.createAuth(data);
            return data;
        }
    }

    public void logout(String authToken) throws DataAccessException {
        if (auth.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        } else
            auth.deleteAuth(authToken);
    }

    private AuthData generateAuth(String username){
        String authToken = UUID.randomUUID().toString();
        return new AuthData(authToken, username);
    }
}
