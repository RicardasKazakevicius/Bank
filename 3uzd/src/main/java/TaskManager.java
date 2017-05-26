import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import spark.Request;
import spark.Response;


public class TaskManager {
    User admin = null;
    public String register(Request request, Response response) throws Exception{
        UUID uid = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d"); 
        User user;
        int status = 0;
        try{
            String body = request.body();
            if("".equals(body)){
                response.status(400);
                throw new Exception("Invalid input");
            }
            user = JsonTransformer.fromJson(request.body(), User.class);
            
            if( (user.password == null) || 
                    (user.first_name == null) ){
                response.status(400);
                throw new Exception("Incorect data");  
            }
            user.token = String.valueOf(uid.randomUUID());
            
            if (user.first_name.equals("admin")) {
                admin = user;
                return admin.token;
            } 
            else {    
                status = createAccount(user);  
            }            
        }
        catch(Exception e){
            response.status(400);
            return e.getMessage();
        }
        if (status != 200) {
           response.status(400);
           return "cannot create account";
        }
        return  user.token;
    }
    
    public String login(Request request, Response response) throws Exception{

        String body = request.body();
        if("".equals(body)){
            response.status(400);
            throw new Exception("Invalid input");
        }
        String password;
        User user = JsonTransformer.fromJson(request.body(), User.class);
        if (!user.first_name.isEmpty()) {
            if (user.first_name.equals("admin")) {
                if (user.password.equals(admin.password)) {
                    return admin.token;
                }
                else {
                    response.status(400);
                    return "invalid password";
                }
            }
        }
        Account account;
        
        try{
            account = getAccount(user.id);
            if (account == null) {
                response.status(404);
                throw new Exception("User with specified id not found");
            }  
            password = account.getPassword();
            if (!password.equals(user.password)) {
                response.status(400);
                throw new Exception("invalid password");
            }            
        }
        catch(Exception e){
            return e.getMessage();
        }
      
        return account.getToken();
    }

    public Object getAccountReq(Request request, Response response) { 
        String token = request.headers("Authorization");
        int id = Integer.valueOf(request.params("id"));
        Account account = null;
        try{
            account = getAccount(id);
        }
        catch(Exception e) {
            response.status(404);
            return "Account with id " + id + " not found";
        }
        if (!account.getToken().equals(token)) {
            if ( (admin == null) || (!admin.token.equals(token)) ) {
                response.status(401);
                return "Unauthorized request";
            }
        }
        return account;
    }
    
    public Object getTransactionsReq(Request request, Response response) { 
        String token = request.headers("Authorization");
        int id = Integer.valueOf(request.params("id"));
        Account account = null;
        try{
            account = getAccount(id);
        }    
        catch(Exception e) {
            response.status(404);
            return "Transactions with id " + id + " not found";
        }
        if (!account.getToken().equals(token)) {
            if ( (admin == null) || (!admin.token.equals(token)) ) {
                response.status(401);
                return "Unauthorized request";
            }
        }
        Transaction transaction;
        try{
            transaction = getTransaction(id);
        }
        catch(Exception e) {
            response.status(404);
            return "Transactions with id " + id + " not found";
        }
        return transaction;
    }
    
    public static Account getAccount(int id) throws Exception { 
        Account account = null;
        String name = "bankr:1234"; //"localhost:444"; //bankr:1234 
        int status = 0;
        HttpURLConnection conn = null;
        
        URL url = new URL("http://" + name + "/accounts/" + id); 
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        status = conn.getResponseCode();
        if (status != 200) {
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
    
    public static Transaction getTransaction(int id) throws Exception{ 
        Transaction transaction = null;
        String name = "bankr:1234"; //"localhost:444"; //bankr:1234 
        int status = 0;
        HttpURLConnection conn = null;
        
        URL url = new URL("http://" + name + "/transactions/" + id); 
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        status = conn.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
        String out;
        String output = "";
        while ((out = br.readLine()) != null) {
            output = out;
        }
        transaction = JsonTransformer.fromJson(output, Transaction.class);
    
        if (conn != null)
            conn.disconnect();
    
        return transaction;
    }   
    
    public Object getAccounts (Request request, Response response) throws IOException{
        String token = request.headers("Authorization");
        if (admin == null || !admin.token.equals(token)) {
            response.status(401);
            return "Unauthorized request";
        }
        String otherService = "";
        String name = "bankr:1234"; //"localhost:444"; //bankr:1234 
        String url = new String("http://" + name + "/accounts");
      
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response1 = new StringBuffer();
    
		while ((inputLine = in.readLine()) != null) {
                     
			response1.append(inputLine);
		}
		in.close(); 
                otherService = response1.toString();
        return otherService;
    }
    
    public Object getTransactions (Request request, Response response) throws IOException{
        String token = request.headers("Authorization");
        if (admin == null || !admin.token.equals(token)) {
            response.status(401);
            return "Unauthorized request";
        }
        String otherService = "";
        String name = "bankr:1234"; //"localhost:444"; //bankr:1234 
        String url = new String("http://" + name + "/transactions");
      
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response1 = new StringBuffer();
    
		while ((inputLine = in.readLine()) != null) {
                     
			response1.append(inputLine);
		}
		in.close(); 
                otherService = response1.toString();
        return otherService;
    }
         
    public int createAccount(User user){
        
        String requestString = " { \"first_name\": " + "\""+ user.first_name +"\""+  ", ";
        requestString += "\"last_name\": " + "\"" + user.last_name + "\"" + ", "; 
        requestString += "\"password\": " + "\""+ user.password +"\""+ ",";
        requestString += "\"token\": " + "\""+ user.token +"\""+ "}";  
        System.out.println(requestString);
        HttpURLConnection conn = null;
        int status = 400;
        String name = "bankr:1234"; //"localhost:444"; //bankr:1234 
        try {
            URL url = new URL("http://" + name + "/accounts"); 
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept", "application/json"); //Accept
            
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
           
            conn.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(requestString);
            wr.flush();
            wr.close();
            status = conn.getResponseCode();
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }     
        }
        catch (Exception e) {           
            System.out.println(e);
        }
        
        if (conn != null)
            conn.disconnect();
          
        return status;
    }  
}




/*
        account = null;
        String name = "localhost:444"; //"localhost:444"; //bankr:1234 
        int status = 0;
        HttpURLConnection conn = null;
        try {
          
            URL url = new URL("http://" + name + "/accounts/" + request.params("id")); 
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            status = conn.getResponseCode();
            if (status != 200) {
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
        }
        catch (Exception e) {           
            System.out.println(e);
            response.status(status);
            return "invalid data";
        }
        
        if (conn != null)
            conn.disconnect();
    */