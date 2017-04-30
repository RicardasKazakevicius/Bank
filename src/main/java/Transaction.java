
public class Transaction {
    private int id;
    private int senderId;
    private int receiverId;
    private double amount;
    private String bankName;
    
    public Transaction(int id, int senderId, int receiverId, String bankName,
            int amount) {
        this.id  = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.bankName = bankName;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setReceiversId(int receiversId) {
        this.receiverId = receiversId;
    }
    
    public int getId() {
        return id;
    }
    
    public int getSenderId() {
        return senderId;
    }
    
    public int getReceiverId() {
        return receiverId;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public String getBankName() {
        return bankName;
    }
}
