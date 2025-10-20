package innowise.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Faction implements Runnable {
    private final Map<RobotPart, Integer> inventory;
    private int robotsBuilt;
    private final Factory factory;

    public Faction(Factory factory) {
        this.inventory = new ConcurrentHashMap<>();
        for (RobotPart part : RobotPart.values()) {
            this.inventory.put(part, 0);
        }
        this.robotsBuilt = 0;
        this.factory = factory;
    }

    @Override
    public void run() {
        try {
            while (!factory.isSimulationEnded()) {
                factory.waitForNight();
                if (factory.isSimulationEnded()) {
                    return;
                }
                takeParts();
                buildRobots();
                factory.completeNight();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public int getRobotsBuilt() {
        return robotsBuilt;
    }

    private boolean canBuildRobot() {
        return inventory.get(RobotPart.HEAD) >= 1 && inventory.get(RobotPart.TORSO) >= 1
                && inventory.get(RobotPart.HAND) >= 2 && inventory.get(RobotPart.FOOT) >= 2;
    }

    private void buildRobots() {
        while (canBuildRobot()) {
            inventory.put(RobotPart.HEAD, inventory.get(RobotPart.HEAD) - 1);
            inventory.put(RobotPart.TORSO, inventory.get(RobotPart.TORSO) - 1);
            inventory.put(RobotPart.HAND, inventory.get(RobotPart.HAND) - 2);
            inventory.put(RobotPart.FOOT, inventory.get(RobotPart.FOOT) - 2);
            robotsBuilt++;
        }
    }

    private void takeParts() {
        for (int i = 0; i < Constants.PARTS_CAPACITY_PER_NIGHT; i++) {
            RobotPart part = factory.takeOnePart();
            if (part == null) {
                return;
            }
            inventory.put(part, inventory.getOrDefault(part, 0) + 1);
        }
    }

    public void printStatus() {
        System.out.println("Robots: " + robotsBuilt);
        System.out.println("HEAD: " + inventory.get(RobotPart.HEAD));
        System.out.println("TORSO: " + inventory.get(RobotPart.TORSO));
        System.out.println("HAND: " + inventory.get(RobotPart.HAND));
        System.out.println("FOOT: " + inventory.get(RobotPart.FOOT));
    }

}
