package service;

import dataAccess.*;
public class ClearService {
    private final UserDAO user;
    private final GameDAO game;
    private final AuthDAO auth;

    public ClearService(UserDAO user, GameDAO game, AuthDAO auth){
        this.user = user;
        this.game = game;
        this.auth = auth;
    }

    public void clear() throws DataAccessException{
        user.clear();
        game.clear();
        auth.clear();
    }
}
