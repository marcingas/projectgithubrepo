package pl.marcin.projectgit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.marcin.projectgit.entity.Branch;
import pl.marcin.projectgit.entity.Commit;
import pl.marcin.projectgit.entity.UserGitRepo;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GitHubApiService {
    private final String GITHUB_API_URL = "https://api.github.com";
    private final RestTemplate restTemplate;

    public List<UserGitRepo> getUserRepositories(String username) {
        String apiUrl = GITHUB_API_URL + "/users/" + username + "/repos";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        RequestEntity<Void> request = new RequestEntity<>(httpHeaders, HttpMethod.GET, URI.create(apiUrl));

        ResponseEntity<List<UserGitRepo>> response = restTemplate.exchange(request,
                new ParameterizedTypeReference<List<UserGitRepo>>() {
                });
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

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        RequestEntity<Void> request = new RequestEntity<>(httpHeaders, HttpMethod.GET, URI.create(apiUrl));
        ResponseEntity<List<Branch>> response = restTemplate.exchange(request,
                new ParameterizedTypeReference<List<Branch>>() {
                });

        if (response.getStatusCode().is2xxSuccessful()) {
            List<Branch> branches = response.getBody();
            List<String> branchNames = branches.stream()
                    .map(Branch::getName)
                    .collect(Collectors.toList());
            return branchNames;
        } else {
            throw new RuntimeException("Failed to get repository branches from GitHub API");
        }
    }

    private String getLastCommitSHA(String owner, String repoName, String branch) {
        String apiUrl = GITHUB_API_URL + "/repos/" + owner + "/" + repoName + "/commits/" + branch;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        RequestEntity<Void> request = new RequestEntity<>(httpHeaders, HttpMethod.GET, URI.create(apiUrl));

        ResponseEntity<Commit> response = restTemplate.exchange(request, Commit.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Commit commit = response.getBody();
            if (commit != null) {
                return commit.getSha();
            }
        }
        return null;
    }
}
