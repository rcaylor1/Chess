package dataAccess;

import model.AuthData;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    private final HashMap<String, AuthData> auths = new HashMap<>();

    public void clear() throws DataAccessException{
        auths.clear();
    }

    public AuthData createAuth(String username) throws DataAccessException{
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        auths.put(authToken, authData);
        return authData;
    }

    public AuthData getAuth(AuthData auth) throws DataAccessException{
        if (auths.containsKey(auth.username())){
            return auths.get(auth.username());
        }
        return null;
    }

    public void deleteAuth(AuthData auth) throws DataAccessException{
        auths.remove(auth.username());
    }
}
