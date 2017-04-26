import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static spark.Spark.*;
/**
 *
 * @author Ričardas Kazakevičius
 */
public class Main {
    public static void main(String[] args) {
        AccountsData aData = new AccountsData();
        TransactionsData tData = new TransactionsData();
        
        port(1234);
        
        path("/accounts", () -> {
                      
            post("/:id", (req, res) -> {
                res.header("Path", req.pathInfo());
                res.header("Method", req.requestMethod());
                return AccountController.createAccount(req, res, aData);
            }, new JsonTransformer());
            
            
            get("", (req, res) -> {
                res.header("Path", req.pathInfo());
                res.header("Method", req.requestMethod());
                return AccountController.getAllAccounts(req, res, aData);
                
            }, new JsonTransformer());
            
            get("/:id", (req, res) -> {
                res.header("Path", req.pathInfo());
                res.header("Method", req.requestMethod());
                return AccountController.getAccount(req, res, aData);
            }, new JsonTransformer());
            
            get("/:id/sent", (req, res) -> {
                res.header("Path", req.pathInfo());
                res.header("Method", req.requestMethod());
                return AccountController.getSentTransactions(req, res, aData, tData);
            }, new JsonTransformer());
             
            get("/:id/received", (req, res) -> {
                res.header("Path", req.pathInfo());
                res.header("Method", req.requestMethod());
                return AccountController.getReceivedTransactions(req, res, aData, tData);
            }, new JsonTransformer());
            
            post("", (req, res) -> {
                res.header("Path", req.pathInfo() + "/" + (aData.getAll().size()+1));
                res.header("Method", req.requestMethod());
                return AccountController.createAccount(req, res, aData);
            }, new JsonTransformer());
            
            put("/:id", (req, res) -> {
                res.header("Path", req.pathInfo());
                res.header("Method", req.requestMethod());
                return AccountController.updateAccount(req, res, aData);
            }, new JsonTransformer());
            
            delete("/:id", (req, res) -> {
                res.header("Path", req.pathInfo());
                res.header("Method", req.requestMethod());
                return AccountController.deleteAccount(req, res, aData);
            }, new JsonTransformer());
            
        });
        
        path("/transactions", () -> {
         
           get("", (req, res) -> {
               res.header("PATH", req.pathInfo());
               res.header("Method", req.requestMethod());
               return TransactionController.getAllTransactions(req, res, tData);
           }, new JsonTransformer()); 
           
           get("/:id", (req, res) -> {
               res.header("PATH", req.pathInfo());
               res.header("Method", req.requestMethod());
               return TransactionController.getTransaction(req, res, tData);
           }, new JsonTransformer()); 
           
           post("", (req, res) -> {
               res.header("PATH", req.pathInfo());
               res.header("Method", req.requestMethod());
               return TransactionController.createTransaction(req, res, tData, aData);
           }, new JsonTransformer()); 
           
           put("/:id", (req, res) -> {
               res.header("PATH", req.pathInfo());
               res.header("Method", req.requestMethod());
               return TransactionController.updateTransactiont(req, res, tData, aData);
           }, new JsonTransformer()); 
           
           delete("/:id", (req, res) -> {
               res.header("PATH", req.pathInfo());
               res.header("Method", req.requestMethod());
               return TransactionController.deleteTransaction(req, res, tData);
           }, new JsonTransformer()); 
        });
        
        exception(Exception.class, (e,req,res) -> {
           res.status(HTTP_BAD_REQUEST);
           JsonTransformer jsonTransformer = new JsonTransformer();
           res.body(jsonTransformer.render( new ErrorMessage(e) ));
        });
        
        after((req, rep) -> rep.type("application/json"));
        

    }
}
