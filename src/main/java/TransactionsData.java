
import java.util.*;
import java.util.stream.Collectors;

public class TransactionsData {
    private Map<Integer, Transaction> transactions = new HashMap();
    
    public TransactionsData() {
        
        List<Transaction> transactionArray = Arrays.asList(
            new Transaction(1, 1, 5, 10),
            new Transaction(2, 2, 4, 20),
            new Transaction(3, 1, 3, 5)
        );
       
        transactionArray.forEach(transaction-> {
            this.transactions.put(transaction.getId(), transaction);
        });
    }
    
    public void create(Transaction transaction, AccountsData accountsData) {
        accountsData.get(transaction.getSenderId()).decreaseBalance(transaction.getAmount());
        accountsData.get(transaction.getReceiverId()).increaseBalance(transaction.getAmount());
        transaction.setId(transactions.size() + 1);
        transactions.put(transaction.getId(), transaction);
    }
    
    public void delete(int id) {
        transactions.remove(id);
    }
    
    public Transaction get(int id) {
        return transactions.get(id);
    }
    
    public void update(int id, Transaction transaction) {
        transaction.setId(id);
        transactions.put(id, transaction);
    }
    
    public List<Transaction> getAll() {
        return transactions.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }
}
