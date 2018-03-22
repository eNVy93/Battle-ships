package lt.envy.battleships.service;

import lt.envy.battleships.entity.Event;
import lt.envy.battleships.entity.GameData;
import lt.envy.battleships.entity.User;
import lt.envy.battleships.utils.GameConstants;
import lt.envy.battleships.utils.GameUtilityService;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class LogicService {

    GameLogicService gameLogicService = new GameLogicService();
    GameService gameService = new GameService();
    GameUtilityService utilityService = new GameUtilityService();

//    public void play(String[][] myBoard, String[][] enemyBoard, GameData game, User user, Scanner scanner) throws IOException, ParseException, InterruptedException {
//
//        GameData statusData = gameService.status(game.getGameId());
////        String nextPlayer = statusData.getNextTurnForUserId();
//        while (!GameConstants.FINISHED.equals(game.getStatus())) {
//            while (!statusData.getNextTurnForUserId().equals(user.getUserId())) {
//                oponentsTurn(statusData,user,myBoard,enemyBoard);
//                System.out.println("");
////                gameLogicService.markTheShot(game, user, myBoard, enemyBoard);
////                gameLogicService.drawGameBoard(game,myBoard,enemyBoard);
////                utilityService.shotHistory(statusData);
//                statusData = gameService.status(game.getGameId());
//            }
//            gameLogicService.markTheShot(game,user,myBoard,enemyBoard);
//            gameLogicService.drawGameBoard(game,myBoard,enemyBoard);
//            utilityService.shotHistory(statusData);
//            System.out.println("YOUR TURN. IT'S YOUR TIME TO SHINE. LETS SHOOT SOME SHIPS. TYPE A COORDINATE e.g. L2 TO SHOOT");
//            String target = scanner.nextLine();
//            GameData turnData = gameService.turn(game.getGameId(),user.getUserId(),target);
//            gameLogicService.markTheShot(game,user,myBoard,enemyBoard);
//            gameLogicService.drawGameBoard(game,myBoard,enemyBoard);
//            utilityService.shotHistory(turnData);
//            statusData = turnData;
//
//
//
//        }
//
//    }
//    public void oponentsTurn(GameData game, User user, String[][] myBoard, String[][] enemyBoard) throws IOException, ParseException, InterruptedException {
//        List<Event> eventListFromGame = game.getListOfEvents();
//        List<Event> eventListFromStatus = gameService.status(game.getGameId()).getListOfEvents();
//        if (eventListFromGame.size() < eventListFromStatus.size()) {
//            myBoard = gameLogicService.markTheShot(game, user, myBoard, enemyBoard);
//            gameLogicService.drawGameBoard(game,myBoard,enemyBoard);
//            utilityService.shotHistory(game);
//
//        } else {
//            Thread.sleep(1000);
//            System.out.print("....");
//        }
//    }
}
