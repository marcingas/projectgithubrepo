package pl.marcin.projectgit.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Owner {
    private String login;
    public String getLogin() {
        return login;
    }
}
