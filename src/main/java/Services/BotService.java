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
    //private boolean anotherWay = false;

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
            // Sorting object in surrounding
            var nearestObstacleList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD
                            || item.getGameObjectType() == ObjectTypes.ASTEROID_FIELD
                            || item.getGameObjectType() == ObjectTypes.WORMHOLE)
                    .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var nearestSuperFoodList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.SUPERFOOD)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var nearestFoodList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var nearestPlayerList = gameState.getPlayerGameObjects()
                    .stream().filter(item -> item.id != bot.id)
                    .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var nearestSupernovaList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.SUPERNOVA_PICKUP)
                    .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            // ACTION
            // Out Of Boundaries

            System.console().printf("------------------\nTicks " + gameState.world.currentTick + "\n");
            System.console().printf("Player distance = " + getDistanceBetween(bot, nearestPlayerList.get(0)) + "\n");
            System.console().printf("Player size = " + nearestPlayerList.get(0).getSize() + "\n");
            System.console().printf("Bot size = " + bot.getSize() + "\n");
            System.console().printf("Obstacle Distance  = " + getDistanceBetween(bot, nearestObstacleList.get(0)) + "\n");
            System.console().printf("Torpedo Salvo count = " + bot.torpedoSalvoCount + "\n");
            System.console().printf("Supernova count = " + bot.supernovaAvailable + "\n");
            System.console().printf("Player heading" + bot.currentHeading + "\n");
            
            // Stop Afterburner
            if (bot.getSize() <= 5) {
                playerAction.action = PlayerActions.STOP_AFTERBURNER;
                playerAction.heading = getHeadingBetween(bot, nearestPlayerList.get(0));
            }

            // Supernova
            else if (bot.supernovaAvailable > 0) {
                System.console().printf("Firing Supernova\n");
                playerAction.action = PlayerActions.FireSupernova;
                playerAction.heading = getHeadingBetween(bot, nearestPlayerList.get(0));
            } 
            
            // Torpedo
            else if (bot.torpedoSalvoCount > 0) {
                System.console().printf("Firing Torpedo\n");
                playerAction.action = PlayerActions.FireTorpedoes;
                playerAction.heading = getHeadingBetween(bot, nearestPlayerList.get(0));
            }
            
            // Safe zone
            else if ((getDistanceFromCenter() + 2 * bot.getSize()) > gameState.world.getRadius()) {
                playerAction.heading = goToCenter();
                System.console().printf("Get Away from Out Of Bound\n");
            }
            
            // Jauhin player besar
            else if (nearestPlayerList.get(0).getSize() > bot.getSize() &&
                    (getDistanceBetween(bot, nearestPlayerList.get(0)) <= (bot.getSize() * 1.5 + nearestPlayerList.get(0).getSize()))){
                System.console().printf("Get Away from Nearest Bigger Player\n");
                playerAction.heading = getHeadingAway(nearestPlayerList.get(0));

                // Afterburner
                if (getDistanceBetween(nearestPlayerList.get(0), bot) < 4 * bot.getSize()) {
                    System.console().printf("Start Afterburner to avoid player\n");
                    playerAction.action = PlayerActions.START_AFTERBURNER;
                }
            }
            
            // Jauhin obstacle
            else if (!nearestObstacleList.isEmpty() && (getDistanceBetween(bot, nearestObstacleList.get(0)) <= (nearestObstacleList.get(0).getSize() + bot.getSize()))) {
                playerAction.heading = getHeadingAway(nearestObstacleList.get(0));
                System.console().printf("Get Away from Nearest Obstacle\n");
            } 

            // Cari makan + Start Afterburner (Kalo bisa)
            else {
                System.console().printf("Targeting\n");
                GameObject currentTarget = setTarget(nearestSuperFoodList, nearestFoodList, nearestPlayerList, nearestSupernovaList);
                playerAction.heading = getHeadingBetween(bot, currentTarget);
                if (currentTarget.getGameObjectType() == ObjectTypes.PLAYER && sizeComparisonAfterBurner(bot, currentTarget)) {
                    System.console().printf("Start Afterburner to the target player\n");
                    playerAction.action = PlayerActions.START_AFTERBURNER;
                }
            }
        }
        this.playerAction = playerAction;
    }

    public GameObject setTarget(List<GameObject> superFoodList, List<GameObject> foodList, List<GameObject> playerList, List<GameObject> supernovaList){
        List<GameObject> enemyDistance;
        GameObject retObject = null;
        GameObject temp = null;
        int i = 0;

        if (!supernovaList.isEmpty()){
            while(temp == null && i < supernovaList.size()) {
                enemyDistance = getEnemyDistance(playerList, supernovaList.get(i));
                if (getDistanceBetween(enemyDistance.get(0), supernovaList.get(i)) < getDistanceBetween(bot, supernovaList.get(i))
                        || enemyDistance.get(0).currentHeading != getHeadingBetween(enemyDistance.get(0), supernovaList.get(i))) {
                    // Ada kemungkinan kesini nih!
                    temp = supernovaList.get(i);
                } else i++;
            }

            if (temp != null) {
                if (retObject == null || getDistanceBetween(bot, temp) < getDistanceBetween(bot, retObject)) {
                    // Target : Supernova!
                    System.console().printf("Current Target : Supernova\n");
                    retObject = temp;
                }
            }
        }

        
        else if (playerList.get(0).getSize() < bot.getSize() && (getDistanceBetween(bot, playerList.get(0)) < 4 * bot.getSize())){
            // Target : Enemy
            System.console().printf("Current Target : Enemy \n");
            retObject = playerList.get(0);
        }

        else if (!superFoodList.isEmpty()){
            i = 0;
            temp = null;
            while(temp == null && i < superFoodList.size()) {
                enemyDistance = getEnemyDistance(playerList, superFoodList.get(i));
                if (getDistanceBetween(enemyDistance.get(0), superFoodList.get(i)) < getDistanceBetween(bot, superFoodList.get(i))
                        || enemyDistance.get(0).currentHeading != getHeadingBetween(enemyDistance.get(0), superFoodList.get(i))) {
                    // Ada kemungkinan kesini nih!
                    temp = superFoodList.get(i);
                } else i++;
            }

            if (temp != null) {
                if (retObject == null || getDistanceBetween(bot, temp) < getDistanceBetween(bot, retObject)) {
                    // Target : SuperFood
                    System.console().printf("Current Target : SuperFood\n");
                    retObject = temp;
                }
            }
        }

        else /* if (!foodList.isEmpty()) */{
            i = 0;
            temp = null;
            while(temp == null && i < foodList.size()) {
                enemyDistance = getEnemyDistance(playerList, foodList.get(i));
                if (getDistanceBetween(enemyDistance.get(0), foodList.get(i)) < getDistanceBetween(bot, foodList.get(i))
                        || enemyDistance.get(0).currentHeading != getHeadingBetween(enemyDistance.get(0), foodList.get(i))) {
                    // Ada kemungkinan kesini nih!
                    temp = foodList.get(i);
                } else i++;
            }
            if (temp == null) temp = foodList.get(0);

            if (temp != null) {
                if (retObject == null ||getDistanceBetween(bot, temp) < getDistanceBetween(bot, retObject)) {
                    // Target : Food
                    System.console().printf("Current Target : Food\n");
                    retObject = temp;
                }
            }
        }



        return retObject;
    }

    public boolean sizeComparisonAfterBurner(GameObject bot, GameObject target) {
        boolean startBurner;
        int botSpeed = bot.getSpeed();
        int targetSize = target.getSize();
        double distance = getDistanceBetween(bot, target);

        double timeTarget = Math.sqrt((botSpeed*botSpeed) + distance) - botSpeed;
        double botSizeDuringAfterBurner = timeTarget;

        if (botSizeDuringAfterBurner < targetSize) {
            startBurner = true;
        } else {
            startBurner = false;
        }

        return startBurner;
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
        var direction = toDegrees(Math.atan2(gameState.world.getCenterPoint().y-bot.getPosition().y, gameState.world.getCenterPoint().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }
    private int getHeadingBetween(GameObject object, GameObject otherObject) {
        var direction = toDegrees(Math.atan2(otherObject.getPosition().y - object.getPosition().y,
                otherObject.getPosition().x - object.getPosition().x));
        return (direction + 360) % 360;
    }

    // Ubah ke arah makanan terdekat yang paling jauh dari obstacle
    private int getHeadingAway(GameObject otherObject) {
        var nearestFoodList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

        List<GameObject> nearestFoodDest = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(otherObject, item)))
                    .collect(Collectors.toList());

        var direction = 0;

        int lastFoodInList = 0;
        for (int i = 0; i < nearestFoodList.size(); i++) {
            if (nearestFoodList.get(i).getId() == nearestFoodDest.get(nearestFoodDest.size()).getId()) {
                lastFoodInList = i;
                break;
            }
        }

        for (int i = nearestFoodDest.size()-1; i > 0; i--) {
            int currentFoodInList = 0;
            for (int j = 0; j < nearestFoodList.size(); j++) {
                if (nearestFoodList.get(j).getId() == nearestFoodDest.get(nearestFoodDest.size()).getId()) {
                    currentFoodInList = j;
                    break;
                }
            }  

            if (currentFoodInList > lastFoodInList) {
                break;
            }

            lastFoodInList = currentFoodInList;
        }

        direction = getHeadingBetween(bot, nearestFoodList.get(lastFoodInList));

        return direction;
        //if (!anotherWay){
        //    direction = toDegrees(Math.atan2( otherObject.getPosition().y + 90,
        //            otherObject.getPosition().x + 90));
        //    anotherWay = true;
        //} else {
        //    direction = toDegrees(Math.atan2( otherObject.getPosition().y + 180,
        //            otherObject.getPosition().x + 180));
        //    anotherWay = false;
        //}
        //return (direction + 360) % 360;
    }


    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }
}
