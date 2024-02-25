package dataAccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    private final HashMap<String, AuthData> auths = new HashMap<>();

    public void clear() throws DataAccessException {
        auths.clear();
    }

    public AuthData createAuth(AuthData auth) throws DataAccessException{
        return auths.put(auth.authToken(), auth);
    }

    public AuthData getAuth(String auth) throws DataAccessException{
        if (auths.containsKey(auth)){
            return auths.get(auth);
        }
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException{
        auths.remove(authToken);
    }
}
