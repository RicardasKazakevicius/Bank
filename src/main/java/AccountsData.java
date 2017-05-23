import java.util.*;
import java.util.stream.Collectors;

public class AccountsData {
    
    private Map<Integer, Account> accounts = new HashMap();
    
    public AccountsData() {
        
        List<Account> accountsArray = Arrays.asList(
            new Account(1, "Jonas", "Jonaitis" , 0, "1234", "p1"),
            new Account(2, "Petras", "Petraitis", 5000.40, "P99", "p2"),
            new Account(3, "Ricardas", "Kazakevicius", 200.21, "R3000", "p3")
        );
       
        accountsArray.forEach(account-> {this.accounts.put(account.getId(), account);
        });

    }
    
    public void create(Account account) {
        account.setId(accounts.size() + 1);
        accounts.put(account.getId(), account);
    }
    
    public void delete(int id) {
        accounts.remove(id);
    }
    
    public Account get(int id) {
        return accounts.get(id);
    }
    
    public void update(int id, Account account) {
        account.setId(id);
        accounts.put(id, account);
    }
    
    public List<Account> getAll() {
        return accounts.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }
    
}
