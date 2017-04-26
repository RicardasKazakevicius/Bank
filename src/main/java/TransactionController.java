import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        Account account = null;
      
        Transaction transaction = JsonTransformer.fromJson(request.body(), Transaction.class);
        
        if (!request.pathInfo().equals("/transactions")) {
           account = getAccount(aData, transaction);
        }

        String transactionStatus;
        
        if (account != null) {
            transactionStatus = checkTransaction1(aData, transaction);
        }
        else {
            transactionStatus = checkTransaction(aData, transaction);
        }
        
        if ("OK".equals(transactionStatus)) {
                
            if (account != null) {
                update(account, transaction);
            }
            
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
        
        else if (accountsData.get(transaction.getReceiverId()) == null || 
               ( accountsData.get(transaction.getReceiverId()) == accountsData.get(transaction.getSenderId())))
            return "Invalid receivers id";
        
        else if (transaction.getAmount() <= 0)
            return "Invalid amount";
        
        else if (accountsData.get(transaction.getSenderId()).getBalance() <= transaction.getAmount())
            return "Not enough balance for transaction";
        
        return "OK";
    }
    
    private static String checkTransaction1(AccountsData accountsData, Transaction transaction) {
         
        if (accountsData.get(transaction.getSenderId()) == null)
            return "Invalid senders id";
        
        else if (transaction.getAmount() <= 0)
            return "Invalid amount";
        
        else if (accountsData.get(transaction.getSenderId()).getBalance() <= transaction.getAmount())
            return "Not enough balance for transaction";
        
        return "OK";
    }
    
    private static Account getAccount(AccountsData aData, Transaction transaction) {
        Account account = null;
        try {
                URL url = new URL("http://localhost:80/accounts/" + aData.get(transaction.getReceiverId()).getId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream())));
                String out;
                String output = "";
                while ((out = br.readLine()) != null) {
                    output = out;
                }
                account = JsonTransformer.fromJson(output, Account.class);
               
                conn.disconnect();
                
            } catch (MalformedURLException ex) {
                Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, null, ex);
            }
        return account;
    }
    
    private static void update(Account account, Transaction transaction) {
        try {
                URL url = new URL("http://localhost:80/accounts/" + transaction.getReceiverId() 
                );
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //conn.setDoOutput(true);
                conn.setRequestMethod("PUT");
                
                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }

                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                out.write("{balance: 100000}");
                //out.write("");
                out.close();
                //conn.getInputStream();
                conn.getOutputStream();
                conn.disconnect();
                
            } catch (MalformedURLException ex) {
                Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
}