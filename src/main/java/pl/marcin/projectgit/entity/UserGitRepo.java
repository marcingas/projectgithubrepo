package pl.marcin.projectgit.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserGitRepo {
    private String name;

    private Owner owner ;

    List<Branch> branch;
    @JsonProperty("fork")
    private boolean fork;


    public boolean isFork() {
        return fork;
    }
}
