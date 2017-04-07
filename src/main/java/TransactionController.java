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
            return new ErrorMessage("No transaction found with id " + request.params("id"));
        }
    }
    
    public static Object createTransaction(Request request, Response response,
            TransactionsData tData, AccountsData aData) {
        
        Transaction transaction = JsonTransformer.fromJson(request.body(), Transaction.class);
        if ( (aData.get(transaction.getReceiverId())  != null) && 
                (aData.get(transaction.getSenderId()) != null ) &&
                (aData.get(transaction.getSenderId()).getBalance() >= transaction.getAmount())) {
            tData.create(transaction, aData);
            return "Transaction created";
        }
        else 
            return "Transaction can not be created!";
    }
    
    public static Object updateTransactiont(Request request, Response response, TransactionsData tData) {
        try {
            Transaction transaction = JsonTransformer.fromJson(request.body(), Transaction.class);
            int id = Integer.valueOf(request.params("id"));
            tData.update(id, transaction);
            return "Transaction updated";
             
        } catch(Exception e) {
           response.status(HTTP_NOT_FOUND);
           return new ErrorMessage("No transaction found with id " + request.params("id"));
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
              return new ErrorMessage("No transaction found with id " + request.params("id"));
          }
    }
}