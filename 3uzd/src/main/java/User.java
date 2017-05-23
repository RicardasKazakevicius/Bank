import lombok.Data;
@Data
public class User {
    int id;
    String first_name;
    String last_name;
    String password;
    String token;
    String permissions;
}
