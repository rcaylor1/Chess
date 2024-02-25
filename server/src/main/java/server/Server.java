package server;

import com.google.gson.Gson;
import dataAccess.*;
import model.*;
import service.*;
import spark.*;
import exception.*;

import java.util.Map;
import java.util.Collections;

public class Server {
    private final UserDAO userDAO = new MemoryUserDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);

        createRoutes();
        Spark.awaitInitialization();
        return Spark.port();
    }
    private static void createRoutes(){
        Spark.before((request, response) -> System.out.println("Executing route: " + request.pathInfo()));
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public static void main(String[] args){
        new Server().run(8080);
    }

    private Object clear(Request request, Response response) throws DataAccessException {
        ClearService service = new ClearService(userDAO, gameDAO, authDAO);
        service.clear();
        response.status(200);
        return "{}";
    }

    private Object register(Request req, Response res) {
        Gson gson = new Gson();
        UserService service = new UserService(userDAO, authDAO);
        UserData user = gson.fromJson(req.body(), UserData.class);
//        first method/logic wasn't working so gotta try again lol
        try {
            if (user.password() == null) { //check if it's valid
                res.status(400);
                return gson.toJson(Map.of("message", "Error: bad request")); //saw this method on petshop so hopefully it works
            } else {
                res.status(200);
                AuthData data = service.register(user);
                return gson.toJson(data);
            }
        }
        catch (DataAccessException exception){
            res.status(403);
            return gson.toJson(new ResponseMessage(exception.getMessage()));
        }
    }

    private Object login(Request request, Response response) {
        Gson gson = new Gson();
        LoginRequest loginRequest = gson.fromJson(request.body(), LoginRequest.class);
        UserService service = new UserService(userDAO, authDAO);
        try {
            AuthData returnAuth = service.login(loginRequest);
            response.status(200);
            return gson.toJson(returnAuth);
        } catch (DataAccessException accessException) {
            response.status(401);
            return gson.toJson(new ResponseMessage(accessException.getMessage()));
        }
    }

    private Object logout(Request request, Response response){
        UserService service = new UserService(userDAO, authDAO);
        String authToken = request.headers("authorization");
        try {
            service.logout(authToken);
            response.status(200);
            return "{}";
        }
        catch (DataAccessException accessException) {
            response.status(401);
            return new Gson().toJson(new ResponseMessage(accessException.getMessage()));
        }
    }

}
