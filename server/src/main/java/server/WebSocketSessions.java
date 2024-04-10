package server;

import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketSessions {
    private final ConcurrentHashMap<Integer, HashMap<String, Session>> sessions = new ConcurrentHashMap<>();

    public void addSessionToGame(int gameID, String authToken, Session session) {
        HashMap<String, Session> newSessions = sessions.get(gameID);
        if (newSessions == null) {
            newSessions = new HashMap<>();
            sessions.put(gameID, newSessions);
        }
        newSessions.put(authToken, session);
    }

    public void removeSessionFromGame(int gameID, String authToken) {
        HashMap<String, Session> newSession = sessions.get(gameID);
        if (newSession != null) {
            newSession.remove(authToken);
        }
    }

    public void removeSession(Session session){
        for (HashMap<String, Session> newSession : sessions.values()) {
            newSession.values().removeIf(value -> value.equals(session));
        }
    }

    public HashMap<String, Session> getSessionsForGame(int gameID) {
        return sessions.get(gameID);
    }

}
