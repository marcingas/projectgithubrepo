package pl.marcin.projectgit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.marcin.projectgit.entity.UserGitRepo;
import pl.marcin.projectgit.service.GitHubApiService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GitHubRepoController {
    private final GitHubApiService gitHubApiService;

    @GetMapping("/repositories/{username}")
    public List<UserGitRepo> getRepositories(@PathVariable String username){
        return gitHubApiService.getUserRepositories(username);
    }

}
