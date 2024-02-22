package dataAccess;

import model.AuthData;
import java.util.HashMap;
public class MemoryAuthDAO implements AuthDAO{
    private final HashMap<String, AuthData> auths = new HashMap<>();

    public void clear() throws DataAccessException{
        auths.clear();
    }
}
