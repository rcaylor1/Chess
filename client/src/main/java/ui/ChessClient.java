package ui;

import model.AuthData;
import model.JoinGameRequest;
import model.UserData;

import java.util.Scanner;
import static ui.EscapeSequences.*;
import ui.ServerFacade;

public class ChessClient {
    private Scanner scanner = new Scanner(System.in);
    private State state = State.LOGGEDOUT;
    private ServerFacade facade;


    public ChessClient(){
        System.out.print(SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_TEXT_COLOR_BLUE);
        facade = new ServerFacade("http://localhost:8080");
        try {
            run();
        } catch (ResponseException e){
            throw new RuntimeException(e);
        }
    }

    public void run() throws ResponseException{
        System.out.print("WELCOME! ");
        System.out.println("Please enter the number corresponding to your choice");
        System.out.println("1. Help");
        System.out.println("2. Quit");
        System.out.println("3. Login");
        System.out.println("4. Register");
        System.out.print("[LOGGED-OUT] >>> ");
        String info = scanner.nextLine();
        if (state != State.LOGGEDIN){
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
        UserData newUser = new UserData(username, password, email);
        String registered = facade.register(newUser).authToken();
        postLogin(registered);
    }

    private void postLogin(String authToken) {
        System.out.println("Successfully logged in");
    }
}
