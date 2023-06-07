package pl.marcin.projectgit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.marcin.projectgit.entity.UserGitRepo;

import java.net.URI;
import java.util.List;

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
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to fetch user repositories from GitHub API");
        }


    }
}
