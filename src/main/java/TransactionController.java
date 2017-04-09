import spark.Request;
import spark.Response;

public class TransactionController {
    private static final int HTTP_BAD_REQUEST = 400;
    private static final int HTTP_NOT_FOUND = 404;
    
    public static Object getAllTransactions(Request request, Response response, TransactionsData tData) {
        return tData.getAll();
    }
    
    public static Object getTransaction(Request request, Response response, TransactionsData tData) {
        try {
            int id = Integer.valueOf(request.params("id"));
            Transaction transaction = tData.get(id);
            if (transaction == null) 
                throw new Exception("No transaction found with id " + id);
            
            return transaction;
        } catch(Exception e) {
            response.status(HTTP_NOT_FOUND);
            return new ErrorMessage(e);
        }
    }
    
    public static Object createTransaction(Request request, Response response,
            TransactionsData tData, AccountsData aData) {
        
        Transaction transaction = JsonTransformer.fromJson(request.body(), Transaction.class);
        
        String transactionStatus = checkTransaction(aData, transaction);
        
        if ("OK".equals(transactionStatus)) {
            tData.create(transaction, aData);
            return transactionStatus;
        }
        else {
            response.status(HTTP_BAD_REQUEST);
            return transactionStatus;
        }
    }
    
    public static Object updateTransactiont(Request request, Response response, 
            TransactionsData tData, AccountsData aData){
        try {
            Transaction transaction = JsonTransformer.fromJson(request.body(), Transaction.class);
            int id = Integer.valueOf(request.params("id"));
            
            if (tData.get(id) == null)
                throw new Exception("No transaction found with id " + request.params("id"));
            
            String transactionStatus = checkTransaction(aData, transaction);
        
            if ("OK".equals(transactionStatus)) {
                tData.update(id, transaction);
                return "Transaction updated";
            }           
                
            response.status(HTTP_BAD_REQUEST);
            return new ErrorMessage(transactionStatus);
                
        } catch(Exception e) {
           response.status(HTTP_NOT_FOUND);
           return new ErrorMessage(e);
        }
    }
     
    public static Object deleteTransaction(Request request, Response response, TransactionsData tData) {
        try {
            int id = Integer.valueOf(request.params("id"));
            Transaction transaction = tData.get(id);
            if (transaction == null) 
                throw new Exception("No transaction found with id " + id);
            
            tData.delete(id);
            return "Transaction with id " + id + " deleted";
          } catch(Exception e) {
              response.status(HTTP_NOT_FOUND);
              return new ErrorMessage(e);
          }
    }
    
    private static String checkTransaction(AccountsData accountsData, Transaction transaction) {
         
        if (accountsData.get(transaction.getSenderId()) == null)
            return "Invalid senders id";
        
        else if (accountsData.get(transaction.getReceiverId()) == null)
            return "Invalid receivers id";
        
        else if (transaction.getAmount() <= 0)
            return "Invalid amount";
        
        else if (accountsData.get(transaction.getSenderId()).getBalance() <= transaction.getAmount())
            return "Not enough balance for transaction";
        
        return "OK";
    }
    
}