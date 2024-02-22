package dataAccess;

import model.GameData;

import java.util.HashMap;
public class MemoryGameDAO implements GameDAO{
    private final HashMap<String, GameData> games = new HashMap<>();

    public void clear() throws DataAccessException{
        games.clear();
    }
}
