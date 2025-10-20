package com.innowise.skynet;

import com.innowise.skynet.model.Faction;
import com.innowise.skynet.model.Factory;


public class Main {
    public static void main(String[] args) {
        try {
            Factory factory = new Factory();
            Faction world = new Faction(factory);
            Faction wednesday = new Faction(factory);

            Thread factoryThread = new Thread(factory);
            Thread worldFactionThread = new Thread(world);
            Thread wednesdayFactionThread = new Thread(wednesday);
            factoryThread.start();
            worldFactionThread.start();
            wednesdayFactionThread.start();

            factoryThread.join();
            worldFactionThread.join();
            wednesdayFactionThread.join();

            factory.printStatus();
            System.out.println();
            System.out.println("World faction:");
            world.printStatus();
            System.out.println();
            System.out.println("Wednesday faction:");
            wednesday.printStatus();
            System.out.println();
            if (world.getRobotsBuilt() > wednesday.getRobotsBuilt()) {
                System.out.println("World faction wins");
            } else if (world.getRobotsBuilt() < wednesday.getRobotsBuilt()) {
                System.out.println("Wednesday faction wins");
            } else {
                System.out.println("Nobody wins");
            }
        } catch (InterruptedException e) {
            System.err.println("Simulation was interrupted");
            Thread.currentThread().interrupt();
        }
    }
}