package innowise.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Factory implements Runnable {

    private final List<RobotPart> storage = new LinkedList<>();
    private final Random random = new Random();
    private boolean isDay = true;
    private final Lock lock = new ReentrantLock();
    private boolean simulationEnded = false;
    private int factionsCompletedNight = 0;
    private final Condition dayCondition;
    private final Condition nightCondition;

    public Factory() {
        this.dayCondition = lock.newCondition();
        this.nightCondition = lock.newCondition();
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < Constants.TOTAL_DAYS; i++) {
                waitForDay();
                produceParts();
                startNight();
                waitForDay();
            }
            endSimulation();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public RobotPart takeOnePart() {
        lock.lock();
        try {
            if (storage.isEmpty()) {
                return null;
            }
            return storage.removeFirst();
        } finally {
            lock.unlock();
        }
    }

    public boolean isSimulationEnded() {
        return simulationEnded;
    }

    private void waitForDay() throws InterruptedException {
        lock.lock();
        try {
            while (!isDay) {
                dayCondition.await();
            }
        } finally {
            lock.unlock();
        }
    }

    public void waitForNight() throws InterruptedException {
        lock.lock();
        try {
            while (isDay && !simulationEnded) {
                nightCondition.await();
            }
        } finally {
            lock.unlock();
        }

    }

    private void startNight() {
        lock.lock();
        isDay = false;
        factionsCompletedNight = 0;
        nightCondition.signalAll();
        lock.unlock();
    }

    public void completeNight() {
        lock.lock();
        factionsCompletedNight++;
        if (factionsCompletedNight == Constants.TOTAL_FACTIONS) {
            isDay = true;
            dayCondition.signalAll();
        }
        lock.unlock();
    }

    private void endSimulation() {
        lock.lock();
        simulationEnded = true;
        nightCondition.signalAll();
        lock.unlock();
    }

    private void produceParts() {
        lock.lock();
        try {
            for (int i = 0; i <= random.nextInt(Constants.PARTS_PER_DAY); i++) {
                storage.add(RobotPart.values()[random.nextInt(RobotPart.values().length)]);
            }
        } finally {
            lock.unlock();
        }
    }


    public void printStatus() {
        System.out.println("Factory storage:");
        System.out.println("HEAD: " + storage.stream().filter(s -> s == RobotPart.HEAD).count());
        System.out.println("TORSO: " + storage.stream().filter(s -> s == RobotPart.TORSO).count());
        System.out.println("HAND: " + storage.stream().filter(s -> s == RobotPart.HAND).count());
        System.out.println("FOOT: " + storage.stream().filter(s -> s == RobotPart.FOOT).count());
    }
}
