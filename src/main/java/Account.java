
public class Account {
    private int id;
    private String name;
    private String surname;
    private double balance;
    
    public Account(int id, String name, String surname, double balance) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.balance = balance;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void increaseBalance(double amount) {
        balance += amount;
        balance = balance * 100;
        balance = Math.round(balance);
        balance = balance / 100;
    }
    
    public void decreaseBalance(double amount) {
        balance -= amount;
        balance = balance * 100;
        balance = Math.round(balance);
        balance = balance / 100;
    }
    
    public String getName() {
        return name;
    }
    
    public String getSurname() {
        return surname;
    }

    public int getId() {
        return id;
    }
    
    public double getBalance() {
        return balance;
    }
}
