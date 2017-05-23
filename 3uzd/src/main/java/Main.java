import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        TaskManager task = new TaskManager();
        port(888);
      
        path("/accounts", () -> {
            get("/:id", (req, res) -> {
            res.header("Path", req.pathInfo());
            res.header("Method", req.requestMethod());
            return task.getAccountReq(req, res);    
            }, new JsonTransformer()); 
            
            get("", (req, res) -> {
            res.header("Path", req.pathInfo());
            res.header("Method", req.requestMethod());
            return task.getAccounts(req, res);    
            }, new JsonTransformer());
        });
        
        path("/transactions", () -> {
            get("/:id", (req, res) -> {
            res.header("Path", req.pathInfo());
            res.header("Method", req.requestMethod());
            return task.getTransactionsReq(req, res);    
            }, new JsonTransformer()); 
            
            get("", (req, res) -> {
            res.header("Path", req.pathInfo());
            res.header("Method", req.requestMethod());
            return task.getTransactions(req, res);    
            }, new JsonTransformer());
        });
        
        path("", () -> {
            post("/login", (request, response) -> {
            return task.login(request, response);
            }, new JsonTransformer());
            
              post("/register", (request, response) -> {
             return task.register(request, response);
             }, new JsonTransformer());
        });
        /*
        path("/accounts", () -> {
                      
            post("/:id", (req, res) -> {
                res.header("Path", req.pathInfo());
                res.header("Method", req.requestMethod());
                return task.createAccount(req, res);
            }, new JsonTransformer());
            
            
            get("", (req, res) -> {
                res.header("Path", req.pathInfo());
                res.header("Method", req.requestMethod());
                return task.getAllAccounts(req, res);
                
            }, new JsonTransformer());
            
            get("/:id", (req, res) -> {
                res.header("Path", req.pathInfo());
                res.header("Method", req.requestMethod());
                return task.getAccount(req, res);
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
           
           post("/:id", (req, res) -> {
               res.header("PATH", req.pathInfo());
               res.header("Method", req.requestMethod());
               return TransactionController.createSTransaction(req, res, tData, aData);
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
*/
       
        
        exception(Exception.class, (e,req,res) -> {
           res.status(400);
           JsonTransformer jsonTransformer = new JsonTransformer();
           res.body(jsonTransformer.render( new ErrorMessage(e) ));
        });
        
        after((req, rep) -> rep.type("application/json"));

    }


}
