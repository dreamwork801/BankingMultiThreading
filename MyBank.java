import java.util.Random;

/* Name: Zach Colby
 * Course CNT 4717 - Fall 2015
 * Assignment title: Program 2 - An Application Using Cooperating 
 * and Synchronized Multiple Threads In Java Using Locks
 * Date: Tuesday, September 15th, 2015
 */

//Class to illustrate threads that create transactions for a bank account
public class MyBank {
    // Create threads
    Thread Thread1 = new Thread(new Deposit(), "Thread 1");
    Thread Thread2 = new Thread(new Deposit(), "Thread 2");
    Thread Thread3 = new Thread(new Deposit(), "Thread 3");
    Thread Thread4 = new Thread(new Withdraw());
    Thread Thread5 = new Thread(new Withdraw());
    Thread Thread6 = new Thread(new Withdraw());
    Thread Thread7 = new Thread(new Withdraw());
    Thread Thread8 = new Thread(new Withdraw());
    Account myAccount = new Account();
    public static final int deposit = 200;

    public static void main(String[] args) {
        new MyBank();
    }

    public MyBank() {
        // Start threads
        Thread1.start();
        Thread2.start();
        Thread3.start();
        //Thread4.start();
        //Thread5.start();
        //Thread6.start();
        //Thread7.start();
        //Thread8.start();
    }

    // The thread class for making a withdraw from the account
    class Withdraw implements Runnable {

        public Withdraw() {
        }

        // Override the run() method to tell the system what the thread will do
        public void run() {
            for (int i = 0; i < 10; i++)
                System.out.print("test");
        }
    }

    // The thread class for making a deposit into the account
    class Deposit implements Runnable {

        // Constructor 
        public Deposit() {
        }

        // Tell the thread how to run 
        public void run() {
            int i = 0;
            while (true){
                // Check to see if the account is locked
                if (!myAccount.isLocked){
                    // Try and lock it
                    try {
                        myAccount.SetLocked();
                    }
                    catch (IllegalThreadStateException e){
                        System.out.println("Unsucessful Lock, Trying again...");
                    }
                    
                    int depositAmount = getRandomAmount(deposit);
                    myAccount.Deposit(depositAmount);
                    System.out.println(Thread.currentThread().getName() + " deposits $" + depositAmount + "\t Balance is $" + myAccount.getAmount());
                    myAccount.Unlock();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (i>100) { break; }
                else { i++; }
            }
        }
    }
    
    class Account {
        boolean isLocked;
        int Amount = 0;
        
        public int getAmount() { return Amount; }
        public void Deposit(int amount) { Amount += amount; }
        public void Withdraw(int amount) { Amount -= amount; }
        public boolean IsLocked() { return isLocked; }
        public void SetLocked() {
            if (isLocked){
                throw new IllegalThreadStateException();
            }
            else{
                this.isLocked = true;
            }
        }
        public void Unlock(){
            this.isLocked = false;
        }
    }
    
    // Returns a random amount between 1 and max
    public int getRandomAmount(int max) {
        Random randomGenerator = new Random();
        return randomGenerator.nextInt(max) + 1;
    }
}
