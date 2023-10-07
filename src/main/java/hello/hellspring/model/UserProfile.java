package hello.hellspring.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfile {
    private String id;
    private String name;
    private String pw;
    public UserProfile(String id, String name, String pw) {
        super();
        this.id = id;
        this.name = name;
        this.pw = pw;
    }
}
