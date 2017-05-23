
public class Account {
    private int id;
    private String first_name; // first_name
    private String last_name; // last_name
    private double balance;
    private String password;
    private String token;
    
    public Account(int id, String name, String surname, double balance, String password,
String token) {
        this.id = id;
        this.first_name = name;
        this.last_name = surname;
        this.balance = balance;
        this.password = password;
	this.token = token;
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
        return first_name;
    }
    
    public String getSurname() {
        return last_name;
    }

    public int getId() {
        return id;
    }
    
    public double getBalance() {
        return balance;
    }
    public String getPassword() {
        return password;
    }
        public String getToken() {
        return token;
    }
}
