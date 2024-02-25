package server;

import com.google.gson.Gson;
import dataAccess.*;
import dataAccess.Exceptions.*;
import model.*;
import service.*;
import spark.*;

public class Server {
    private final UserDAO userDAO = new MemoryUserDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);

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

    private Object register(Request req, Response res) throws DataAccessException{
        UserService service = new UserService(userDAO, authDAO);
        UserData userData = new Gson().fromJson(req.body(), UserData.class);

        try {
            AuthData authData = service.register(userData);
            res.status(200);
            return new Gson().toJson(authData);
        } catch (BadRequest badRequest){
            res.status(400);
            return new Gson().toJson(badRequest.getMessage());
        } catch (AlreadyTaken alreadyTaken){
            res.status(403);
            return new Gson().toJson(alreadyTaken.getMessage());
        } catch (DataAccessException exception){
            res.status(500);
            return new Gson().toJson(exception.getMessage());
        }
    }
}
