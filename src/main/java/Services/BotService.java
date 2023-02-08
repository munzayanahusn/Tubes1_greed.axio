package Services;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;

    public BotService() {
        this.playerAction = new PlayerAction();
        this.gameState = new GameState();
    }


    public GameObject getBot() {
        return this.bot;
    }

    public void setBot(GameObject bot) {
        this.bot = bot;
    }

    public PlayerAction getPlayerAction() {
        return this.playerAction;
    }

    public void setPlayerAction(PlayerAction playerAction) {
        this.playerAction = playerAction;
    }

    public void computeNextPlayerAction(PlayerAction playerAction) {
        playerAction.action = PlayerActions.FORWARD;
        playerAction.heading = new Random().nextInt(360);

        // GoTo Nearest Smaller Player
        if (!gameState.getGameObjects().isEmpty()) {
            var playerList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.PLAYER)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .filter(item -> getSmallerSize(item, bot))
                    .collect(Collectors.toList());

            playerAction.heading = getHeadingBetween(playerList.get(0));
        }

        // Gather Nearest SuperFood
        if (!gameState.getGameObjects().isEmpty()) {
            var superFoodList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.SUPERFOOD)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            playerAction.heading = getHeadingBetween(superFoodList.get(0));
        }

        // Gather Nearest Food
        if (!gameState.getGameObjects().isEmpty()) {
            var foodList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            playerAction.heading = getHeadingBetween(foodList.get(0));
        }

        // GetAway from Bigger Player
        if (!gameState.getGameObjects().isEmpty()) {
            var playerList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.PLAYER)
                    .filter(item -> getBiggerSize(item, bot))
                    .filter(item -> (getDistanceBetween(item, bot) < 2))
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            playerAction.heading = getHeadingAway(playerList.get(0));
        }
        // GetAway from Gas Cloud
        if (!gameState.getGameObjects().isEmpty()) {
            var playerList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD)
                    .filter(item -> (getDistanceBetween(item, bot) < 2))
                    .collect(Collectors.toList());

            playerAction.heading = getHeadingAway(playerList.get(0));
        }

        // GetAway from Asteroid Fields
        if (!gameState.getGameObjects().isEmpty()) {
            var playerList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.ASTEROID_FIELD)
                    .filter(item -> (getDistanceBetween(item, bot) < 2))
                    .collect(Collectors.toList());

            playerAction.heading = getHeadingAway(playerList.get(0));
        }

        /*
        if (!gameState.getGameObjects().isEmpty()) {
            var foodList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            playerAction.heading = getHeadingBetween(foodList.get(0));
        }
        */

        this.playerAction = playerAction;
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        updateSelfState();
    }

    private void updateSelfState() {
        Optional<GameObject> optionalBot = gameState.getPlayerGameObjects().stream().filter(gameObject -> gameObject.id.equals(bot.id)).findAny();
        optionalBot.ifPresent(bot -> this.bot = bot);
    }

    private boolean getSmallerSize(GameObject object1, GameObject object2){
        return object1.getSize() < object2.getSize();
    }
    private boolean getBiggerSize(GameObject object1, GameObject object2){
        return object1.getSize() > object2.getSize();
    }
    private double getDistanceBetween(GameObject object1, GameObject object2) {
        var triangleX = Math.abs(object1.getPosition().x - object2.getPosition().x);
        var triangleY = Math.abs(object1.getPosition().y - object2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    private int getHeadingBetween(GameObject otherObject) {
        var direction = toDegrees(Math.atan2(otherObject.getPosition().y - bot.getPosition().y,
                otherObject.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }
    private int getHeadingAway(GameObject otherObject) {
        var direction = toDegrees(Math.atan2(otherObject.getPosition().y + 90,
                otherObject.getPosition().x + 90));
        return (direction + 360) % 360;
    }

    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }


}