package server;

import dataAccess.*;
import spark.Request;
import spark.Response;
import spark.Spark;

public class Server {
    private final UserDAO userDAO = new MemoryUserDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();

    private final ClearHandler clearHandler = new ClearHandler(userDAO, gameDAO, authDAO);


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

//        Spark.delete("/db", this::clear);

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
        return clearHandler.clear(request,response);
    }
}
