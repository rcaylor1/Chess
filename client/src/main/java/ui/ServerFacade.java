package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(UserData newUser) throws ResponseException{
        var path = "/user";
        return this.makeRequest("POST", path, newUser, null, AuthData.class);
    }

    public AuthData login(UserData newUser) throws ResponseException{
        var path = "/session";
        return this.makeRequest("POST", path, newUser, null, AuthData.class);
    }

    public GameData logout(String authToken) throws ResponseException{
        var path = "/session";
        return this.makeRequest("DELETE", path, null, authToken, GameData.class);
    }

    public GameData createGame(GameData game, String authToken) throws ResponseException{
        var path = "/game";
//        GameData newGame = new GameData(1, null, null, game, new ChessGame());
        return this.makeRequest("POST", path, game, authToken, GameData.class);
    }

    public GameData[] listGames(String authToken) throws ResponseException{
        var path = "/game";
        ListGameRequest listGames = this.makeRequest("GET", path, null, authToken, ListGameRequest.class);
        return listGames.games();
    }

    public void joinGame(JoinGameRequest join, String authToken) throws ResponseException{
        var path = "/game";
        this.makeRequest("PUT", path, join, authToken, null);
    }

    private <T> T makeRequest(String method, String path, Object request, String header, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (header != null){
                http.setRequestProperty("authorization", header);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException("failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
