package org.example;

import java.security.SecureRandom;

// A Cashier is a CONSUMER: it repeatedly takes the next customer ID from the
// shared CheckoutQueue and "serves" them (simulated with sleep).
public class Cashier implements Runnable {
    // Special ID that means "no more customers, close your lane" (a "poison pill").
    public static final int CLOSE_LANE = -1;

    private static final SecureRandom generator = new SecureRandom();
    private final int cashierId;
    private final CheckoutQueue queue; // shared with Customers and other Cashiers

    private int customersServed = 0;
    private long totalServiceTimeMs = 0;

    public Cashier(int cashierId, CheckoutQueue queue) {
        this.cashierId = cashierId;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                int customerId = queue.blockingGet(); // waits here if the line is empty
                if (customerId == CLOSE_LANE) {
                    break; // the store is closing; stop serving
                }

                int serviceTime = 1000 + generator.nextInt(2000); // 1-3 s per customer
                System.out.printf("Cashier %d starts serving customer %d%n", cashierId, customerId);
                Thread.sleep(serviceTime);

                customersServed++;
                totalServiceTimeMs += serviceTime;
                System.out.printf("Cashier %d finished customer %d (%d ms)%n",
                        cashierId, customerId, serviceTime);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // shift was cut short; leave the loop
        }
        System.out.printf("Cashier %d closing lane. Served %d customers.%n",
                cashierId, customersServed);
    }

    public int getCashierId() { return cashierId; }
    public int getCustomersServed() { return customersServed; }
    public long getTotalServiceTimeMs() { return totalServiceTimeMs; }
}
