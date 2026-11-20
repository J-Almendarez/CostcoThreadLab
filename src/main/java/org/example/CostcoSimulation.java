package org.example;

import java.util.Scanner;

public class CostcoSimulation {

    // how much one cashier gets paid per hour
    private static final double CASHIER_PAY_PER_HOUR = 18.00;

    public static void main(String[] args) throws InterruptedException {
        Scanner input = new Scanner(System.in);

        // ---- step 1: get the settings from the user ----
        System.out.print("How many cashiers? ");
        int numCashiers = input.nextInt();

        System.out.print("How many customers? ");
        int numCustomers = input.nextInt();

        System.out.print("Max size of the checkout line? ");
        int lineSize = input.nextInt();

        System.out.println();
        System.out.printf("Starting simulation: %d cashiers, %d customers, line size %d%n%n",
                numCashiers, numCustomers, lineSize);

        // start the timer so we know how long the whole thing took
        long startTime = System.currentTimeMillis();

        // ---- step 2: make the ONE shared line that everyone uses ----
        CheckoutQueue queue = new CheckoutQueue(lineSize);

        // ---- step 3: make and start the cashiers ----
        Cashier[] cashiers = new Cashier[numCashiers];
        Thread[] cashierThreads = new Thread[numCashiers];

        for (int i = 0; i < numCashiers; i++) {
            cashiers[i] = new Cashier(i + 1, queue);
            cashierThreads[i] = new Thread(cashiers[i]);
            cashierThreads[i].start(); // cashier waits at blockingGet() until someone is in line
        }

        // ---- step 4: make and start the customers ----
        Thread[] customerThreads = new Thread[numCustomers];

        for (int i = 0; i < numCustomers; i++) {
            customerThreads[i] = new Thread(new Customer(i + 1, queue));
            customerThreads[i].start();
        }

        // ---- step 5: wait for every customer to get in line ----
        for (int i = 0; i < numCustomers; i++) {
            customerThreads[i].join();
        }

        // ---- step 6: tell the cashiers the store is closing ----
        for (int i = 0; i < numCashiers; i++) {
            queue.blockingPut(Cashier.CLOSE_LANE);
        }

        // ---- step 7: wait for all the cashiers to finish ----
        for (int i = 0; i < numCashiers; i++) {
            cashierThreads[i].join();
        }

        long totalTimeMs = System.currentTimeMillis() - startTime;

        // ---- step 8: print the results ----
        System.out.println();
        System.out.println("========== RESULTS ==========");
        System.out.printf("Cashiers: %d | Customers: %d | Line size: %d%n",
                numCashiers, numCustomers, lineSize);
        System.out.printf("Customers served: %d%n", queue.getCustomersServed());
        System.out.printf("Average wait in line: %.2f seconds%n", queue.getAverageWaitMs() / 1000.0);
        System.out.printf("Longest wait in line: %.2f seconds%n", queue.getMaxWaitMs() / 1000.0);
        System.out.printf("Total simulation time: %.2f seconds%n", totalTimeMs / 1000.0);

        System.out.println();
        System.out.println("Customers served by each cashier:");
        for (int i = 0; i < numCashiers; i++) {
            System.out.printf("  Cashier %d: %d customers%n",
                    cashiers[i].getCashierId(), cashiers[i].getCustomersServed());
        }

        // 1 sec = 1 min in store
        // so total seconds / 60 = hours the cashiers were working
        double simulatedHours = (totalTimeMs / 1000.0) / 60.0;
        double laborCost = numCashiers * CASHIER_PAY_PER_HOUR * simulatedHours;
        System.out.println();
        System.out.printf("Labor cost (1 sec = 1 store minute): $%.2f%n", laborCost);
        System.out.printf("Labor cost per hour of staffing: $%.2f%n", numCashiers * CASHIER_PAY_PER_HOUR);
        System.out.println("=============================");

        input.close();
    }
}

