package dataAccess;

import model.GameData;
import java.util.ArrayList;

public interface GameDAO {
    public void clear() throws DataAccessException;
    public void createGame(GameData game) throws DataAccessException;
    public GameData getGame(Integer gameID) throws DataAccessException;
    public ArrayList<GameData> listGames() throws DataAccessException;
    public void updateGame(GameData game) throws DataAccessException;

    public int incID();
}
