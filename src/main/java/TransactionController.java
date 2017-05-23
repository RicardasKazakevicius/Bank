import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import spark.Request;
import spark.Response;

public class TransactionController {
    private static final int HTTP_BAD_REQUEST = 400;
    private static final int HTTP_NOT_FOUND = 404;
    public static String name = "banks:80";//"localhost:50"; //banks:80 
    
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
        String transactionStatus = "not found";
        String bankr = "bankr";
        String banks = "banks";
        
        Transaction transaction = JsonTransformer.fromJson(request.body(), Transaction.class);
        
        if (transaction.getBankName().equals(banks)) { 
            try {
                account = getAccount(aData, transaction.getReceiverId());
            }
            catch(Exception ex) {
                response.status(HTTP_NOT_FOUND);
                return new ErrorMessage("Account " + transaction.getReceiverId() + " not found"
                        + " in Bank " + banks);
            }

            transactionStatus = checkTransaction1(aData, transaction);

            if ("OK".equals(transactionStatus)) {
                update(account, transaction, true, transaction.getReceiverId());
                tData.create(transaction, aData);
                return transactionStatus;
            }
        }
        else if (transaction.getBankName().equals(bankr)) {
            transactionStatus = checkTransaction(aData, transaction);
            
            if ("OK".equals(transactionStatus)) {
                tData.create(transaction, aData);
                return transactionStatus;
            }
        }
        else {
            response.status(HTTP_NOT_FOUND);  
            return new ErrorMessage("Bank " + transaction.getBankName() + " not found");
        }
        
        response.status(HTTP_BAD_REQUEST);
        return transactionStatus;  
    }
    
    public static Object createSTransaction(Request request, Response response,
            TransactionsData tData, AccountsData aData) {
        Account accountS = null;
        Account accountR = null;
        String transactionStatus = "not found";
        String bankr = "bankr";
        String banks = "banks";
        
        Transaction transaction = JsonTransformer.fromJson(request.body(), Transaction.class);
             
        try {
            accountS = getAccount(aData, transaction.getSenderId());
        }
        catch(Exception ex) {
            response.status(HTTP_NOT_FOUND);
            return new ErrorMessage("Account " + transaction.getSenderId() + " not found"
                    + " in Bank " + banks);
        }

        if (accountS.getBalance() < transaction.getAmount()) 
            transactionStatus = "Not enough balance";
        else 
            transactionStatus = "OK";

        if ("OK".equals(transactionStatus)) {
            
            accountR = aData.get(transaction.getReceiverId());
          
            if (accountR != null) {
                update(accountS, transaction, false, transaction.getSenderId());
                accountR.increaseBalance(transaction.getAmount());
                return transactionStatus;
            } else {
                response.status(HTTP_NOT_FOUND);
                return new ErrorMessage("Account " + transaction.getReceiverId() + " not found"
                    + " in Bank " + bankr);
            }
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
    
    private static Account getAccount(AccountsData aData, int id) throws MalformedURLException, ProtocolException, IOException {
        Account account = null;

        URL url = new URL("http://" + name + "/accounts/" + id); //aData.get(transaction.getReceiverId()).getId()
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

        if (conn != null)
            conn.disconnect();

        return account;
    }
    
    private static void update(Account account, Transaction transaction, boolean increase, int id) {
        
        URL url = null;
        try {
            url = new URL("http://" + name + "/accounts/" + id);
        } catch(MalformedURLException ex) {
            ex.printStackTrace();
        }
        HttpURLConnection conn = null;       
        DataOutputStream dataOutputStream = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json"); //Accept
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            dataOutputStream = new DataOutputStream(conn.getOutputStream());
            double balance = 0;
            if (increase == true)
                balance = account.getBalance() + transaction.getAmount();
            else
                balance = account.getBalance() - transaction.getAmount();
            
            dataOutputStream.writeBytes("{\"balance\": " + balance + "}");
                        
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }   
        } catch(IOException ex) {
            ex.printStackTrace();
        }  finally {
            if (dataOutputStream != null) {
                try {
                    dataOutputStream.flush();
                    dataOutputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
/*
public static Object createSimBankTransaction(Request request, Response response,
                TransactionsData tData, AccountsData aData) throws IOException {
        
        Transaction transaction = JsonTransformer.fromJson(request.body(), Transaction.class);
       
        URL url = null;
        try {
            url = new URL("http://" + name + "/transactions");
        } catch(MalformedURLException ex) {
            ex.printStackTrace();
        }
        HttpURLConnection conn = null;       
        DataOutputStream dataOutputStream = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json"); //Accept Content-Type
            conn.setDoOutput(true);
            dataOutputStream = new DataOutputStream(conn.getOutputStream());
            dataOutputStream.writeBytes("{\"sender_id\": " +transaction.getSenderId() + "," +
                    "\"recipient_id\": " + transaction.getReceiverId() + "," + 
                    "\"amount\": " + transaction.getAmount() + "}");                        
            
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }   

        } catch(Exception ex) {
            response.status(conn.getResponseCode());
            ex.printStackTrace();
            return "Something wrong";
        }  finally {
            if (dataOutputStream != null) {
                try {
                    dataOutputStream.flush();
                    dataOutputStream.close();
                } catch (Exception ex) {
                    System.out.println(ex);
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        
        Account ac = aData.get(transaction.getReceiverId());
        if (ac == null) {
            response.status(HTTP_NOT_FOUND);
            return "User " + transaction.getReceiverId() + " not found in bankr";
        }
        ac.increaseBalance(transaction.getAmount());
        return "OK";    
    }
*/
