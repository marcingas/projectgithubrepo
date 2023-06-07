package pl.marcin.projectgit.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserGitRepo {
    private String name;
    private String ownerLogin;
    Branch branch;
}
