package server;

import spark.*;
import dataAccess.*;
import com.google.gson.Gson;

import service.ClearService;

public class ClearHandler {

    ClearService service;
    public ClearHandler(UserDAO user, GameDAO game, AuthDAO auth){
        service = new ClearService(user, game, auth);
    }

    private Object clearServer(Request request, Response response) throws DataAccessException{
            service.clear();
            response.status(200);
            return new Gson().toJson(null);
//            response.status(500);
//            return new Gson().toJson(new ClearHandler(exception.getMessage()));

    }
}
