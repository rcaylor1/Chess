package server;

import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
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
}
