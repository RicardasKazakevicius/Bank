import spark.Request;
import spark.Response;

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
            return new ErrorMessage(e);
        }
    }
    
    public static Object getSentTransactions(Request request, Response response, 
            AccountsData accountData, TransactionsData tData) {
        try {
            int id = Integer.valueOf(request.params("id"));
            
            if (accountData.get(id) == null) {
                throw new Exception("No account found with id " + id);
            }
            
            if (tData.getAllSent(id).isEmpty()) {
                response.status(HTTP_NOT_FOUND);
                return new ErrorMessage("No sent transactions found with account with id " +  request.params("id"));
            }
            return tData.getAllSent(id);
            
        } catch(Exception e) {
            response.status(HTTP_NOT_FOUND);
            return new ErrorMessage(e);
        }
    }
    
    public static Object getReceivedTransactions(Request request, Response response, 
            AccountsData accountData, TransactionsData tData) {
        try {
            int id = Integer.valueOf(request.params("id"));
            
            if (accountData.get(id) == null) {
                throw new Exception("No account found with id " + id);
            }
            
            if (tData.getAllReceived(id).isEmpty()) {
                response.status(HTTP_NOT_FOUND);
                return new ErrorMessage("No sent transactions found with account with id " +  request.params("id"));
            }
            return tData.getAllReceived(id);
            
        } catch(Exception e) {
            response.status(HTTP_NOT_FOUND);
            return new ErrorMessage(e);
        }
    } 
    
    public static Object createAccount(Request request, Response response, AccountsData accountData) {
        Account account = JsonTransformer.fromJson(request.body(), Account.class);
        String accountStatus = checkAccount(account);
        
        if ("OK".equals(accountStatus)) {
            accountData.create(account);
            return accountStatus;
        }
        
        response.status(HTTP_BAD_REQUEST);
        return new ErrorMessage(accountStatus);
    }
    
    public static Object updateAccount(Request request, Response response, AccountsData accountData) {
        try {
            Account account = JsonTransformer.fromJson(request.body(), Account.class);
            int id = Integer.valueOf(request.params("id"));
            
            if (accountData.get(id) == null) {
                throw new Exception("No account found with id " + request.params("id"));
            }
            
            String accountStatus = checkAccount(account);
        
            if ("OK".equals(accountStatus)) {
                
                accountData.update(id, account);
                return accountStatus;
            }
            response.status(HTTP_BAD_REQUEST);
            return new ErrorMessage(accountStatus);
             
        } catch(Exception e) {
           response.status(HTTP_NOT_FOUND);
           return new ErrorMessage(e);
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
              return new ErrorMessage(e);
          }
    }
    
    private static String checkAccount(Account account) {
        
        if ( (account.getName() == null) || ("".equals(account.getName())) )
            return "No user name specified";
        else if ( (account.getSurname() == null) || ("".equals(account.getSurname())) )
            return "No user surname specified";
        
        return "OK";
    }
      
}
