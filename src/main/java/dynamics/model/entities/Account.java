package dynamics.model.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Account {

    @Id
    private String loginId;
    private String name;
    private Role role;
    private String password;


    public enum Role {
        Admin, User
    }
}
