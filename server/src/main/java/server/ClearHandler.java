package server;

import spark.*;
import dataAccess.*;

import service.ClearService;

public class ClearHandler {

    ClearService service;
    public ClearHandler(UserDAO user, GameDAO game, AuthDAO auth){
        service = new ClearService(user, game, auth);
    }

    public Object clearServer(Request request, Response response) throws DataAccessException{
            service.clear();
            response.status(200);
            return "{}";
    }
}
