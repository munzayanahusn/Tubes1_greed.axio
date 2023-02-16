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
    private boolean anotherWay = false;

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

    public List<GameObject> getNearestPlayerList() {
        return gameState.getPlayerGameObjects().stream()
                    .filter(item -> item.id != bot.id)
                    .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());
    }

    public List<GameObject> getFoodList() {
        return gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                    .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());
    }

    public List<GameObject> getSuperFoodList() {
        return gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.SUPERFOOD)
                    .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());
    }

    public void computeNextPlayerAction(PlayerAction playerAction) {
        Console console = System.console();

        if (!gameState.getGameObjects().isEmpty()) {
            var nearestSuperFoodList = getSuperFoodList();
            var nearestFoodList = getFoodList();
            var nearestPlayerList = getNearestPlayerList();

            // Showing Log
            System.console().printf("=======================\nTicks " + gameState.world.currentTick + "\n");
            System.console().printf("size: " + bot.getSize() + " pos: " + bot.position.x + "," + bot.position.y + "\n");
            System.console().printf("heading: " + bot.currentHeading + " speed: " + bot.speed + "\n");
            System.console().printf("code: " + bot.effectsCode + " salvo: " + bot.torpedoSalvoCount + "\n");
            System.console().printf("np dist: " + getDistanceBetween(bot, nearestPlayerList.get(0)));
            System.console().printf("np size: " + nearestPlayerList.get(0).getSize() + "\n");

            // Setting Target Priority
            GameObject target = setTarget(nearestSuperFoodList, nearestFoodList, nearestPlayerList);

            // Entering Gas Cloud For The First Time, Starting Afterburner
            if (bot.effectsCode == 4 || bot.effectsCode == 6) {
                System.console().printf("Entering Gas Cloud, Starting Afterburner\n");
                playerAction.action = PlayerActions.StartAfterburner;
                playerAction.heading = getHeadingBetween(bot, target);
            } 
            // Still Inside Gas Cloud, Keep Chasing Target
            else if (bot.effectsCode == 5 || bot.effectsCode == 7) {
                System.console().printf("Inside Gas Cloud, Keep Chasing\n");
                playerAction.action = PlayerActions.Forward;
                playerAction.heading = getHeadingBetween(bot, target);
            }
            // Afterburner Is Not Needed, Stop Afterburner 
            else if (bot.effectsCode%2 == 1 && (bot.getSize() <= nearestPlayerList.get(0).getSize() + 5 || bot.getSize() <= 20)) {
                System.console().printf("Stoping After Burner\n");
                playerAction.action = PlayerActions.StopAfterburner;
                playerAction.heading = getHeadingBetween(bot, target);
            }
            // Torpedo Fireable, Fire Torpedo
            else if (bot.torpedoSalvoCount > 0 && bot.getSize() > 20 && bot.effectsCode <= 1
                    && nearestPlayerList.get(0).effectsCode == 0 && nearestPlayerList.get(0).speed <= 60) {
                System.console().printf("Firing Torpedo\n");
                playerAction.action = PlayerActions.FireTorpedoes;
                playerAction.heading = getHeadingBetween(bot, nearestPlayerList.get(0));
            } 
            // Bot Approachin World Bounds, Go To Center
            else if ((getDistanceFromCenter() + 2 * bot.getSize()) > gameState.world.getRadius()) {
                System.console().printf("Get Away from Out Of Bound\n");
                playerAction.action = PlayerActions.Forward;
                // Bigger Enemy Detected On The Way To The Center, Turn 90 Degrees
                if (nearestPlayerList.get(0).getSize() > bot.getSize() &&
                    (getDistanceBetween(bot, nearestPlayerList.get(0)) < (nearestPlayerList.get(0).getSize())*5 + bot.getSize()*2)) {
                    System.console().printf("Bigger player detected\nEscaping\n");
                    playerAction.heading = getHeadingAway(bot, nearestPlayerList.get(0)) + 90;
                } 
                // Road Is Clear, Proceed To The Center
                else {
                    playerAction.heading = goToCenter();
                }
            } 
            // Bigger Enemy Approaching, Head To The Other Direction
            else if (nearestPlayerList.get(0).getSize() > bot.getSize() &&
                    (getDistanceBetween(bot, nearestPlayerList.get(0)) < (nearestPlayerList.get(0).getSize())*5 + bot.getSize()*2)){
                System.console().printf("Get Away from Nearest Bigger Player\n");
                playerAction.action = PlayerActions.Forward;
                playerAction.heading = getHeadingAway(bot, nearestPlayerList.get(0));
            } 
            // Chase Initial Target
            else {
                System.console().printf("Targeting\n");
                // Chasing A LOT Smaller Enemy, Start Afterburner
                if (target == nearestPlayerList.get(0) && bot.effectsCode%2 == 0
                    && bot.getSize() - target.getSize() > getDistanceBetween(bot, target)/(bot.speed) + 20) {
                    System.console().printf("Starting After Burner Towards Enemy\n");
                    playerAction.action = PlayerActions.StartAfterburner;
                } else {
                    playerAction.action = PlayerActions.Forward;
                }
                playerAction.heading = getHeadingBetween(bot, target);
            }
        }
        this.playerAction = playerAction;
    }

    public GameObject setTarget(List<GameObject> superFoodList, List<GameObject> foodList, List<GameObject> playerList){
        List<GameObject> enemyDistance;
        GameObject retObject = null;
        GameObject temp = null;
        int i = 0;
        
        // Nearest Enemy Is Smaller, Target Enemy
        if (playerList.get(0).getSize() < bot.getSize()){
            System.console().printf("Current Target : Enemy \n");
            retObject = playerList.get(0);
        } 
        // SuperFood Exist
        else if (!superFoodList.isEmpty()){
            i = 0;
            temp = null;
            // SuperFood is Closest To Player Or No Enemy Heading Towards It, Target Superfood
            while(temp == null && i < superFoodList.size()) {
                enemyDistance = getEnemyDistance(playerList, superFoodList.get(i));
                if (getDistanceBetween(bot, superFoodList.get(i)) < getDistanceBetween(enemyDistance.get(0), superFoodList.get(i))
                    || enemyDistance.get(0).currentHeading != getHeadingBetween(enemyDistance.get(0), superFoodList.get(i))) {
                    temp = superFoodList.get(i);
                } else i++;
            }
            if (temp != null) {
                if (retObject == null || getDistanceBetween(bot, temp) < getDistanceBetween(bot, retObject)) {
                    System.console().printf("Current Target : SuperFood\n");
                    retObject = temp;
                }
            }
        }
        // Food Exist
        else if (!foodList.isEmpty()){
            i = 0;
            temp = null;
            // Food is Closest To Player Or No Enemy Heading Towards It, Target Food
            while(temp == null && i < foodList.size()) {
                enemyDistance = getEnemyDistance(playerList, foodList.get(i));
                if (getDistanceBetween(bot, foodList.get(i)) < getDistanceBetween(enemyDistance.get(0), foodList.get(i))
                        || enemyDistance.get(0).currentHeading != getHeadingBetween(enemyDistance.get(0), foodList.get(i))) {
                    temp = foodList.get(i);
                } else i++;
            }
            if (temp != null) {
                if (retObject == null ||getDistanceBetween(bot, temp) < getDistanceBetween(bot, retObject)) {
                    System.console().printf("Current Target : Food\n");
                    retObject = temp;
                }
            }
            // Target Nearest Food Unconditionally
            else temp = foodList.get(0);
        }
        return retObject;
    }

    public List<GameObject> getEnemyDistance(List<GameObject> playerList, GameObject object) {
        return playerList.stream()
                .sorted(Comparator.comparing(item -> getDistanceBetween(item, object)))
                .collect(Collectors.toList());
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
        GameObject worldCenter = new GameObject(null, null, null, null, gameState.world.getCenterPoint(), null);
        return getHeadingBetween(bot, worldCenter);
    }
    private int getHeadingBetween(GameObject object, GameObject otherObject) {
        var direction = toDegrees(Math.atan2(otherObject.getPosition().y - object.getPosition().y,
                otherObject.getPosition().x - object.getPosition().x));
        return (direction + 360) % 360;
    }
    private int getHeadingAway(GameObject bot, GameObject otherObject) {
        var direction = toDegrees(Math.atan2(
                bot.getPosition().x - otherObject.getPosition().x,
                bot.getPosition().y - otherObject.getPosition().y));
        return direction;
    }
    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }
}