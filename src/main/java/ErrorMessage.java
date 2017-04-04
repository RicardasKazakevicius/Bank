
public class ErrorMessage {
    private String message;

    public ErrorMessage(String message) {
        this.message = message;
    }

    public ErrorMessage(String message, String... args) {
        this.message = String.format(message, args);
    }

    public ErrorMessage(Exception e) {
        this.message = e.getMessage();
    }
    
}
