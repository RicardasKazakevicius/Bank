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
    public static String name = "localhost"; //banks
    
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
                account = getAccount(aData, transaction);
            }
            catch(Exception ex) {
                response.status(HTTP_NOT_FOUND);
                return new ErrorMessage("Account " + transaction.getReceiverId() + " not found"
                        + " in Bank " + banks);
            }

            transactionStatus = checkTransaction1(aData, transaction);

            if ("OK".equals(transactionStatus)) {
                update(account, transaction);
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
    
    private static Account getAccount(AccountsData aData, Transaction transaction) throws MalformedURLException, ProtocolException, IOException {
        Account account = null;

        URL url = new URL("http://" + name + ":80/accounts/" + transaction.getReceiverId()); //aData.get(transaction.getReceiverId()).getId()
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
    
    private static void update(Account account, Transaction transaction) {
        
        URL url = null;
        try {
            url = new URL("http://" + name + ":80/accounts/" + transaction.getReceiverId());
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
            double balance = account.getBalance() + transaction.getAmount();
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


//get
        /*
        String input = "";
        
        URL url = null;
        try {
            url = new URL("http://" + name + ":80/accounts/" + aData.get(transaction.getReceiverId()).getId());
        } catch(MalformedURLException ex) {
            ex.printStackTrace();
        }
        HttpURLConnection conn = null;
        DataOutputStream dataOutputStream = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            //conn.setRequestProperty("Content-Type", "application/json"); //Accept
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            conn.setDoInput(true);
            conn.setDoOutput(true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
           
            while ((line = in.readLine()) != null) {
                    input += line;
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
    */
        

//update
        /*
        try {
            
            URL url = new URL("http://" + name + ":80/accounts/" + transaction.getReceiverId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Accept", "application/json");
            
                    OutputStreamWriter out = new OutputStreamWriter(
            conn.getOutputStream());
            out.write("{\"balance\": 111111}");
            out.close();
            conn.getInputStream();
            
            conn.setRequestMethod("PUT");

            if (conn.getResponseCode() != 200) {           
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

    

            conn.disconnect();

      
            
            String requestString = "{\"balance\": 99999}";   
            conn.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

            wr.writeBytes(requestString);
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));

            String inputLine;
            StringBuffer response2 = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                    response2.append(inputLine);
            }
            in.close();
            System.out.println(response2);
             
                
            } catch (MalformedURLException ex) {
                Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, null, ex);
            }
            */



              /*
                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                out.write("{\"balance\": 100000}");
                //out.write("");
                out.close();
                conn.getInputStream();
                //conn.getOutputStream();
*/