package ui;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.JoinGameRequest;
import model.UserData;

import java.util.Arrays;
import java.util.Scanner;
import static ui.EscapeSequences.*;
import ui.ServerFacade;

public class ChessClient {
    private Scanner scanner = new Scanner(System.in);
    private State state = State.LOGGED_OUT;
    private ServerFacade facade;
    private GamePlay newBoard;


    public ChessClient(){
        System.out.print(SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_TEXT_COLOR_BLUE);
        facade = new ServerFacade("http://localhost:8080");
        try {
            run();
        } catch (ResponseException e){
            throw new RuntimeException(e);
        }
        newBoard = new GamePlay();
    }

    public void run() throws ResponseException{
        state = State.LOGGED_OUT;
        System.out.print("WELCOME! ");
        System.out.println("Please enter the number corresponding to your choice");
        System.out.println("1. Help");
        System.out.println("2. Quit");
        System.out.println("3. Login");
        System.out.println("4. Register");
        System.out.println();
        System.out.print("[" + state + "] >>> ");
        String info = scanner.nextLine();
        if (state != State.LOGGED_IN){
            switch(info){
                case "1" -> {
                    System.out.println("1. Help: Displays text about what actions you can take");
                    System.out.println("2. Quit: Exits the program");
                    System.out.println("3. Login: Logs in registered user");
                    System.out.println("4. Register: Enter registration information");
                    System.out.println();
                    run();
                }
                case "2" -> System.out.println("Saying goodbye is death by a thousand cuts");
                case "3" -> login();
                case "4" -> register();
                default -> run();
            }
        }
    }

    private void login() throws ResponseException{
        System.out.println("Please enter your username and password when prompted");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.println();
        UserData newUser = new UserData(username, password, null);
        String loggedIn = facade.login(newUser).authToken();
        postLogin(loggedIn);
    }

    private void register() throws ResponseException{
        System.out.println("Please enter your username, password, and email when prompted");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.println();
        UserData newUser = new UserData(username, password, email);
        String registered = facade.register(newUser).authToken();
        postLogin(registered);
    }

    private void postLogin(String authToken) throws ResponseException {
        state = State.LOGGED_IN;
        System.out.println();
        System.out.println("Please enter the number one of the following options");
        System.out.println("1. Help");
        System.out.println("2. Logout");
        System.out.println("3. Create game");
        System.out.println("4. List games");
        System.out.println("5. Join game");
        System.out.println("6. Join as observer");
        System.out.println();
        System.out.print("[" + state + "] >>> ");
        String info = scanner.nextLine();
        switch(info){
            case "1" -> {
                System.out.println("1. Help: Displays text about what actions you can take");
                System.out.println("2. Logout: Logs out user and returns to beginning");
                System.out.println("3. Create game: Creates a new game");
                System.out.println("4. List games: Lists all current games");
                System.out.println("5. Join game: Allows user to join existing game");
                System.out.println("6. Join as observer: Allows user to observe existing game");
                System.out.println();
                postLogin(authToken);
            }
            case "2" -> logout(authToken);
            case "3" -> createGame(authToken);
            case "4" -> listGames(authToken);
            case "5" -> joinGame(authToken);
            case "6" -> joinObserver(authToken);
            default -> postLogin(authToken);
        }
    }

    private void logout(String authToken) throws ResponseException{
        facade.logout(authToken);
        run();
    }

    private void createGame(String authToken) throws ResponseException{
        System.out.print("Please enter a name for the new game: ");
        String gameName = scanner.nextLine();
        GameData newGame = new GameData(0, null, null, gameName, new ChessGame());
        facade.createGame(newGame, authToken);
        postLogin(authToken);
    }

    private void listGames(String authToken) throws ResponseException{
        System.out.println("Current games available: ");
        GameData[] gamesList = facade.listGames(authToken);
        for (int i=0; i < gamesList.length; i++){
            System.out.print(i+1 + ": \n\t");
            GameData newGame = gamesList[i];
            System.out.println("Name: " + newGame.gameName());
            System.out.println("\tPlayers: " + newGame.whiteUsername() + " and " + newGame.blackUsername());
        }
    }

    private void joinGame(String authToken) throws ResponseException{
        listGames(authToken);
        System.out.print("Please enter the number of the desired game: ");
        int gameNumber = Integer.parseInt(scanner.nextLine());
        System.out.print("Please enter the color of the desired color in all caps: ");
        String playerColor = scanner.nextLine();
        facade.joinGame(new JoinGameRequest(playerColor, gameNumber), authToken);
        newBoard.printBoard();
    }

    private void joinObserver(String authToken) throws ResponseException{
        listGames(authToken);
        System.out.print("Please enter the number of the desired game to watch: ");
        int gameNumber = Integer.parseInt(scanner.nextLine());
        facade.joinGame(new JoinGameRequest(null, gameNumber), authToken);
        newBoard.printBoard();
    }
}
