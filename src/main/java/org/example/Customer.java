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

    }
}
