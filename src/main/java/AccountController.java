import spark.Request;
import spark.Response;
/**
 *
 * @author ricardas
 */
public class AccountController {
    private static final int HTTP_BAD_REQUEST = 400;
    private static final int HTTP_NOT_FOUND = 404;
    
    public static Object getAllAccounts(Request request, Response response, AccountsData accountData) {
        return accountData.getAll();
    }
    
    public static Object getAccount(Request request, Response response, AccountsData accountData) {
        try {
            int id = Integer.valueOf(request.params("id"));
            Account account = accountData.get(id);
            if (account == null) 
                throw new Exception("No account found with id " + id);
            
            return account;
        } catch(Exception e) {
            response.status(HTTP_NOT_FOUND);
            return new ErrorMessage("No account found with id " + request.params("id"));
        }
    }
    
    public static Object getSentTransactions(Request request, Response response, 
            AccountsData accountData, TransactionsData tData) {
        try {
            int id = Integer.valueOf(request.params("id"));
            
            if (tData.getAllSent(id).isEmpty()) 
                throw new Exception("No sent transactions found with this account");
            
            return tData.getAllSent(id);
        } catch(Exception e) {
            response.status(HTTP_NOT_FOUND);
            return new ErrorMessage("No sent transactions found with this account");
        }
    }
    
    public static Object getReceivedTransactions(Request request, Response response, 
            AccountsData accountData, TransactionsData tData) {
        try {
            int id = Integer.valueOf(request.params("id"));
            
            if (tData.getAllReceived(id).isEmpty()) 
                throw new Exception("No received transactions found with this account");
            
            return tData.getAllReceived(id);
        } catch(Exception e) {
            response.status(HTTP_NOT_FOUND);
            return new ErrorMessage("No received transactions found with this account");
        }
    }
    
    
    public static Object createAccount(Request request, Response response, AccountsData accountData) {
        Account account = JsonTransformer.fromJson(request.body(), Account.class);
        accountData.create(account);
        return "Account created";
    }
    
    public static Object updateAccount(Request request, Response response, AccountsData accountData) {
        try {
            Account account = JsonTransformer.fromJson(request.body(), Account.class);
            int id = Integer.valueOf(request.params("id"));
            accountData.update(id, account);
            return "Account updated";
             
        } catch(Exception e) {
           response.status(HTTP_NOT_FOUND);
           return new ErrorMessage("No account found with id " + request.params("id"));
        }
    }
     
    public static Object deleteAccount(Request request, Response response, AccountsData accountData) {
        try {
            int id = Integer.valueOf(request.params("id"));
            Account account = accountData.get(id);
            if (account == null) 
                throw new Exception("No account found with id " + id);
            
            accountData.delete(id);
            return "Account with id " + id + " deleted";
          } catch(Exception e) {
              response.status(HTTP_NOT_FOUND);
              return new ErrorMessage("No account found with id " + request.params("id"));
          }
    }
      
}
