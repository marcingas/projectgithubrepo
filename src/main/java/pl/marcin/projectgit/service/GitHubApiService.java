package pl.marcin.projectgit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import pl.marcin.projectgit.entity.Branch;
import pl.marcin.projectgit.entity.Commit;
import pl.marcin.projectgit.entity.UserGitRepo;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GitHubApiService {
    private final String GITHUB_API_URL = "https://api.github.com";


    public List<UserGitRepo> getUserRepositories(String username, HttpHeaders headers) {
        String apiUrl = GITHUB_API_URL + "/users/" + username + "/repos";

        WebClient webClient = WebClient.create();

        Mono<ResponseEntity<List<UserGitRepo>>> responseMono = webClient
                .get()
                .uri(URI.create(apiUrl))
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .retrieve()
                .toEntityList(new ParameterizedTypeReference<UserGitRepo>() {
                });
        ResponseEntity<List<UserGitRepo>> response = responseMono.block();


        if (response.getStatusCode().is2xxSuccessful()) {
            List<UserGitRepo> allRepositories = response.getBody();

            List<UserGitRepo> nonForkRepositories = allRepositories.stream()
                    .filter(repo -> !repo.isFork())
                    .collect(Collectors.toList());

            nonForkRepositories.forEach(repo -> {
                List<String> branches = getRepositoryBranches(repo.getOwner().getLogin(), repo.getName());
                List<Branch> branchList = branches.stream()
                        .map(branchName -> {
                            Branch branch = new Branch();
                            branch.setName(branchName);
                            String lastCommitSHA = getLastCommitSHA(repo.getOwner().getLogin(), repo.getName(), branchName);
                            branch.setLastCommitSha(lastCommitSHA);
                            return branch;
                        })
                        .collect(Collectors.toList());
                repo.setBranch(branchList);
            });
            return nonForkRepositories;
        } else {
            throw new RuntimeException("Failed to get user repositories from GitHub API");
        }
    }

    private List<String> getRepositoryBranches(String owner, String repoName) {
        String apiUrl = GITHUB_API_URL + "/repos/" + owner + "/" + repoName + "/branches";

        WebClient webClient = WebClient.create();

        Mono<ResponseEntity<List<Branch>>> responseMono = webClient
                .get()
                .uri(URI.create(apiUrl))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntityList(new ParameterizedTypeReference<Branch>() {
                });
        ResponseEntity<List<Branch>> response = responseMono.block();


        if (response != null && response.getStatusCode().is2xxSuccessful()) {
            List<Branch> branches = response.getBody();

            if (branches != null) {
                return branches.stream()
                        .map(Branch::getName)
                        .collect(Collectors.toList());
            } else {
                throw new NullPointerException("Branches list is empty");
            }
        } else {
            throw new RuntimeException("Failed to get repository branches from GitHub API");
        }
    }

    private String getLastCommitSHA(String owner, String repoName, String branch) {
        String apiUrl = GITHUB_API_URL + "/repos/" + owner + "/" + repoName + "/commits/" + branch;

        WebClient webClient = WebClient.create();
        Mono<ResponseEntity<Commit>> responseMono = webClient
                .get()
                .uri(URI.create(apiUrl))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(Commit.class);


        ResponseEntity<Commit> response = responseMono.block();

        if (response != null && response.getStatusCode().is2xxSuccessful()) {
            Commit commit = response.getBody();
            if (commit != null) {
                return commit.getSha();
            }
        }
        return null;
    }
}
