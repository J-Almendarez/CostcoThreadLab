package org.example;
import java.security.SecureRandom;


public class Customer implements Runnable {

    private static final SecureRandom generator = new SecureRandom();

    private final int customerId;
    private final CheckoutQueue queue;

    // Constructor
    public Customer(int customerId, CheckoutQueue queue) {
        this.customerId = customerId;
        this.queue = queue;
    }

    @Override
    public void run() {

        try {
            // shopping time is between 1-5 secs
            int shoppingTime = 1000 + generator.nextInt(5000);

            System.out.printf(
                    "Customer %d is shopping.%n", customerId
            );

            Thread.sleep(shoppingTime);

            System.out.printf(
                    "Customer %d finished shopping and joined the checkout line.%n",
                    customerId
            );

            // joinning the checkout queue
            queue.blockingPut(customerId);

        }
        catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    public int getCustomerId() {
        return customerId;
    }
}
