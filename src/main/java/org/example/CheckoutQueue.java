package org.example;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

// CheckoutQueue = the checkout line at Costco
// This is the shared buffer between the Customers and the Cashiers


public class CheckoutQueue {

    // The line with IDs
    // put() makes a customer wait if the line is full
    // take() makes a cashier wait if the line is empty
    private ArrayBlockingQueue<Integer> line;

    //tracks queue time
    private HashMap<Integer, Long> joinTimes = new HashMap<>();

    // stats for the analysis
    private long totalWaitMs = 0;
    private long maxWaitMs = 0;
    private int customersServed = 0;

    // capacity
    public CheckoutQueue(int capacity) {
        line = new ArrayBlockingQueue<>(capacity);

    }


    public void blockingPut(int customerId) throws InterruptedException {

        if (customerId != Cashier.CLOSE_LANE) {
            saveJoinTime(customerId);
        }

        // if the line is full the customer just waits here until there's room
        line.put(customerId);

        if (customerId != Cashier.CLOSE_LANE) {
            System.out.printf("Customer %d got in line (line size: %d)%n", customerId, line.size());
        }
    }


    public int blockingGet() throws InterruptedException {
        int customerId = line.take();

        // figure out how long this customer waited
        if (customerId != Cashier.CLOSE_LANE) {
            recordWait(customerId);
        }

        return customerId;

    }


    private synchronized void saveJoinTime(int customerId) {
        joinTimes.put(customerId, System.currentTimeMillis());
    }


    private synchronized void recordWait(int customerId) {
        long joinTime = joinTimes.get(customerId);
        long waitTime = System.currentTimeMillis() - joinTime;

        totalWaitMs += waitTime;
        customersServed++;

        if (waitTime > maxWaitMs) {
            maxWaitMs = waitTime;
        }
    }


    public synchronized double getAverageWaitMs() {
        if (customersServed == 0) {
            return 0; // so we dont divide by 0
        }
        return (double) totalWaitMs / customersServed;
    }


    public synchronized long getMaxWaitMs() {
        return maxWaitMs;
    }


    public synchronized int getCustomersServed() {
        return customersServed;
    }


    public int getLineSize() {
        return line.size();
    }


}
