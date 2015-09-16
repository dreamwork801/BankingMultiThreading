import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* Name: Zach Colby
 * Course CNT 4717 - Fall 2015
 * Assignment title: Program 2 - An Application Using Cooperating 
 * and Synchronized Multiple Threads In Java Using Locks
 * Date: Tuesday, September 15th, 2015
 */

//Class to illustrate threads that create transactions for a bank account
public class MyBank {
    // Create 3 deposit and 5 withdrawal threads
    Thread Thread1 = new Thread(new Deposit(), "Thread 1");
    Thread Thread2 = new Thread(new Deposit(), "Thread 2");
    Thread Thread3 = new Thread(new Deposit(), "Thread 3");
    Thread Thread4 = new Thread(new Withdraw(), "Thread 4");
    Thread Thread5 = new Thread(new Withdraw(), "Thread 5");
    Thread Thread6 = new Thread(new Withdraw(), "Thread 6");
    Thread Thread7 = new Thread(new Withdraw(), "Thread 7");
    Thread Thread8 = new Thread(new Withdraw(), "Thread 8");
    Account myAccount = new Account();
    public static final int deposit = 200; // deposit max for random seed
    public static final int withdraw = 50; // withdraw max for random seed
    Lock lock = new ReentrantLock();
    Condition nofunds = lock.newCondition();

    public static void main(String[] args) {
        new MyBank();
    }

    public MyBank() {
        System.out.println("Deposit Threads\t\tWithdrawal Threads\tBalance");
        System.out.println("-------------\t\t------------\t\t------------");
        // Start threads
        Thread1.start();
        Thread2.start();
        Thread3.start();
        Thread4.start();
        Thread5.start();
        Thread6.start();
        Thread7.start();
        Thread8.start();
    }

    // The thread class for making a withdraw from the account
    class Withdraw implements Runnable {

        public Withdraw() {
        }

        // Override the run() method to tell the system what the thread will do 
        public void run() {
            int withdrawAmount = getRandomAmount(withdraw);
            while (true){
                try {
                    if(lock.tryLock(1, TimeUnit.SECONDS)){
                        boolean success = myAccount.Withdraw(withdrawAmount);
                        System.out.print("\t\t\t" + Thread.currentThread().getName() + " withdraws $" + withdrawAmount);
                        if (!success){
                            System.out.println(" Withdrawal - Blocked - Insufficient Funds");
                            nofunds.await();
                        }
                        else
                            System.out.println("\tBalance is $" + myAccount.getAmount());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally{
                    //release lock
                    lock.unlock();
                    try {
                        // Sleep to allow other threads to go
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }       
            }
        }
    }

    // The thread class for making a deposit into the account
    class Deposit implements Runnable {

        // Constructor 
        public Deposit() {
        }

        // Override the run() method to tell the system what the thread will do 
        public void run() {
            int depositAmount = getRandomAmount(deposit);
            while (true){
                try {
                    if(lock.tryLock(1, TimeUnit.SECONDS)){
                        myAccount.Deposit(depositAmount);
                        nofunds.signalAll();
                        System.out.println(Thread.currentThread().getName() + " deposits $" + depositAmount + "\t\t\t\tBalance is $" + myAccount.getAmount());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally{
                    //release lock
                    lock.unlock();
                    try {
                        // Sleep to allow other threads to go.
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }       
            }
        }
    }
    
    // Account object class
    class Account {
        int Amount = 0;
        
        public int getAmount() { return Amount; }
        public void Deposit(int amount) { Amount += amount; }
        public boolean Withdraw(int amount) { 
            if (amount > Amount)
                return false;
            Amount -= amount;
            return true;
        }
    }
    
    // Returns a random amount between 1 and max
    public int getRandomAmount(int max) {
        Random randomGenerator = new Random();
        return randomGenerator.nextInt(max) + 1;
    }
}
