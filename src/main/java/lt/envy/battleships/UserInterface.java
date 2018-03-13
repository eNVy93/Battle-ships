package lt.envy.battleships;

import lt.envy.battleships.entity.User;
import lt.envy.battleships.service.UserService;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Scanner;

public class UserInterface {
//TODO use StringBuilder for strings !!!!!
    public void printGreeting(){
        System.out.println("**************" +
                "\nWelcome to the best battleship app.\n" +
                "***************\n");
    }

    public void setUpPlayers(Scanner sc, UserService service){
        System.out.println("Enter name for PlayerOne: \n");
        String playerOneName = sc.nextLine();
        System.out.println("Enter email for PlayerOne: \n");
        String playerOneEmail = sc.nextLine();
        System.out.println("Enter name for PlayerTwo: \n");
        String playerTwoName = sc.nextLine();
        System.out.println("Enter email for PlayerTwo: \n");
        String playerTwoEmail = sc.nextLine();
        try {
            User playerOne = service.createUser(playerOneName, playerOneEmail);
            if(playerOne.getUserId() != null){
                System.out.println("PlayerOne: " + playerOne.getName() + " created");
            } else {
                System.out.println("Player two not created");
            }
            User playerTwo = service.createUser(playerTwoName, playerTwoEmail);
            if(playerTwo.getUserId() != null){
                System.out.println("PlayerTwo: " + playerTwo.getName() + " created");
            } else {
                System.out.println("Player two not created");
            }

        } catch (IOException | ParseException e) {
            System.out.println("Klaida " + e.getMessage());
        }

    }
}
