package server;

import com.google.gson.Gson;
import dataAccess.*;
import dataAccess.Exceptions.DataAccessException;
import model.*;
import service.*;
import spark.*;
import java.util.ArrayList;
import dataAccess.Exceptions.*;
import dataAccess.SQLUserDAO;
import dataAccess.SQLGameDAO;
import dataAccess.SQLAuthDAO;

public class Server {
    UserDAO userDAO;
    GameDAO gameDAO;
    AuthDAO authDAO;
    public Server(){
        try {
            userDAO = new SQLUserDAO();
            gameDAO = new SQLGameDAO();
            authDAO = new SQLAuthDAO();
        }catch (DataAccessException e){
            throw new RuntimeException(e);
        }
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        WebSocketHandler webSocketHandler = new WebSocketHandler();
        Spark.webSocket("/connect", webSocketHandler);

        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);

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

    private Object register(Request request, Response response) throws DataAccessException {
        UserData userData = new Gson().fromJson(request.body(), UserData.class);
        UserService service = new UserService(userDAO, authDAO);
        try {
            AuthData authData = service.register(userData);
            response.status(200);
            return new Gson().toJson(authData);
        } catch (BadRequestException badRequest) {
            response.status(400);
            return new Gson().toJson(new ErrorMessage(badRequest.getMessage()));
        } catch (AlreadyTakenException alreadyTaken) {
            response.status(403);
            return new Gson().toJson(new ErrorMessage(alreadyTaken.getMessage()));
        }
    }

    private Object login(Request request, Response response){
        LoginRequest loginRequest = new Gson().fromJson(request.body(), LoginRequest.class);
        UserService service = new UserService(userDAO, authDAO);
        try {
            AuthData returnAuth = service.login(loginRequest);
            response.status(200);
            return new Gson().toJson(returnAuth);
        } catch (DataAccessException dataAccess) {
            response.status(401);
            return new Gson().toJson(new ErrorMessage(dataAccess.getMessage()));
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
        catch (DataAccessException dataAccess) {
            response.status(401);
            return new Gson().toJson(new ErrorMessage(dataAccess.getMessage()));
        }
    }

    private Object listGames(Request request, Response response) {
        GameService service = new GameService(gameDAO, authDAO);
        String authToken = request.headers("authorization");
        try {
            ArrayList<GameData> games = service.listGames(authToken);
            response.status(200);
            return new Gson().toJson(new ListErrorMessage(games));
        } catch (DataAccessException dataAccess) {
            response.status(401);
            return new Gson().toJson(new ErrorMessage(dataAccess.getMessage()));
        }
    }

    private Object createGame(Request request, Response response) {
        String authToken = request.headers("authorization");
        GameService service = new GameService(gameDAO, authDAO);
        GameData game = new Gson().fromJson(request.body(), GameData.class);
        try {
            int resultGame = service.createGame(authToken, game.gameName());
            response.status(200);
            return new Gson().toJson(new IntErrorMessage(resultGame));
        } catch (BadRequestException badRequest){
            response.status(400);
            return new Gson().toJson(new ErrorMessage(badRequest.getMessage()));
        }
        catch (DataAccessException dataAccess) {
            response.status(401);
            return new Gson().toJson(new ErrorMessage(dataAccess.getMessage()));
        }
    }

    private Object joinGame(Request request, Response response) {
        String authToken = request.headers("authorization");
        GameService service = new GameService(gameDAO, authDAO);
        JoinGameRequest joinRequest = new Gson().fromJson(request.body(), JoinGameRequest.class);
        try {
            service.joinGame(authToken, joinRequest.playerColor(), joinRequest.gameID());
            response.status(200);
            return "{}";
        }
        catch (BadRequestException badRequest) {
            response.status(400);
            return new Gson().toJson(new ErrorMessage(badRequest.getMessage()));
        }
        catch (UnauthorizedException unauthorized) {
            response.status(401);
            return new Gson().toJson(new ErrorMessage(unauthorized.getMessage()));
        }
        catch (DataAccessException dataAccess) {
            response.status(403);
            return new Gson().toJson(new ErrorMessage(dataAccess.getMessage()));
        }
    }
}