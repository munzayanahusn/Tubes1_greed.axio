package Services;

import Enums.*;
import Models.*;

import java.io.*;
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
        Console console = System.console();
        playerAction.action = PlayerActions.FORWARD;

        if (!gameState.getGameObjects().isEmpty()) {
            // GetAway from Gas Cloud
            var nearestObstacleList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD
                            || item.getGameObjectType() == ObjectTypes.ASTEROID_FIELD
                            || item.getGameObjectType() == ObjectTypes.WORMHOLE)
                    .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var nearestFoodList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.SUPERFOOD
                            || item.getGameObjectType() == ObjectTypes.FOOD)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var nearestPlayerList = gameState.getPlayerGameObjects()
                    .stream().filter(item -> item.id != bot.id)
                    .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            // ACTION
            // Out Of Boundaries

            System.console().printf("------------------\nPlayer distance = " + getDistanceBetween(bot, nearestPlayerList.get(0)) + "\n");
            System.console().printf("Player size = " + nearestPlayerList.get(0).getSize() + "\n");
            System.console().printf("Bot size = " + bot.getSize() + "\n");
            if (getDistanceFromCenter() + 2 * bot.getSize() > gameState.world.getRadius()) {
                playerAction.heading = goToCenter();
                System.console().printf("Get Away from Out Of Bound\n");
            } else if (!nearestPlayerList.isEmpty() && (nearestPlayerList.get(0).getSize() > bot.getSize()) &&
                    (getDistanceBetween(bot, nearestPlayerList.get(0)) <= (bot.getSize() * 1.5 + nearestPlayerList.get(0).getSize()))){
                System.console().printf("Get Away from Nearest Bigger Player\n");
                playerAction.heading = getHeadingAway(nearestPlayerList.get(0));
            } else if (!nearestObstacleList.isEmpty() && (getDistanceBetween(bot, nearestObstacleList.get(0)) <= (bot.getSize()*2))) {
                playerAction.heading = getHeadingAway(nearestObstacleList.get(0));
                System.console().printf("Get Away from Nearest Obstacle\n");
            } else {
                System.console().printf("Targeting\n");
                playerAction.heading = getHeadingBetween(setTarget(nearestFoodList, nearestPlayerList));
            }
        }
        this.playerAction = playerAction;
    }

    public GameObject setTarget(List<GameObject> foodList, List<GameObject> playerList){
        List<GameObject> enemyDistance;
        GameObject retObject = null;
        GameObject temp = null;
        int i = 0;

        if (playerList.get(0).getSize() <= bot.getSize()){
            retObject = playerList.get(0);
        }
        if (!foodList.isEmpty()){
            while(temp == null && i < foodList.size()) {
                int finalI = i;
                enemyDistance = playerList
                        .stream().sorted(Comparator.comparing(item->getDistanceBetween(item, foodList.get(finalI))))
                        .collect(Collectors.toList());
                if (getDistanceBetween(enemyDistance.get(0), foodList.get(i)) < getDistanceBetween(bot, foodList.get(i))) {
                    temp = foodList.get(i);
                } else i++;
            }
            if (temp == null) temp = foodList.get(0);
        }
        if (retObject == null || getDistanceBetween(bot, temp) < getDistanceBetween(bot, retObject)) {
            System.console().printf("Gather food\n");
            retObject = temp;
        } else System.console().printf("Go To Smaller Player\n");
        return retObject;
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
    private double getDistanceBetween(GameObject object1, GameObject object2) {
        var triangleX = Math.abs(object1.getPosition().x - object2.getPosition().x);
        var triangleY = Math.abs(object1.getPosition().y - object2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    private double getDistanceFromCenter() {
        var triangleX = Math.abs(bot.getPosition().x - gameState.world.getCenterPoint().x);
        var triangleY = Math.abs(bot.getPosition().y - gameState.world.getCenterPoint().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    private int goToCenter() {
        var direction = toDegrees(Math.atan2(gameState.world.getCenterPoint().y-bot.getPosition().y, gameState.world.getCenterPoint().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }
    private int getHeadingBetween(GameObject otherObject) {
        var direction = toDegrees(Math.atan2(otherObject.getPosition().y - bot.getPosition().y,
                otherObject.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }
    private int getHeadingAway(GameObject otherObject) {
        var direction = toDegrees(Math.atan2( otherObject.getPosition().y + 90,
                otherObject.getPosition().x + 90));
        return (direction + 360) % 360;
    }

    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }


}
