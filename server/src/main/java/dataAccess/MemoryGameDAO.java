package dataAccess;

import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
public class MemoryGameDAO implements GameDAO{
    private final HashMap<Integer, GameData> games = new HashMap<>();

    public void clear() throws DataAccessException{
        games.clear();
    }

    public void createGame(GameData game) throws DataAccessException{
        for (GameData data : games.values()){
            if (!game.gameName().equals(data.gameName())){
                games.put(game.gameID(), game);
            }
        }
        throw new DataAccessException("Game already exists");
    }

    public GameData getGame(GameData game) throws DataAccessException{
        return games.getOrDefault(game.gameID(), null);
    }

    public ArrayList<GameData> listGames() throws DataAccessException{
        return new ArrayList<>(games.values());
    }

    public void updateGame(GameData game) throws DataAccessException{
        if (games.containsKey(game.gameID())){
            games.put(game.gameID(), game);
        } else
            throw new DataAccessException("Unable to update");
    }
}
