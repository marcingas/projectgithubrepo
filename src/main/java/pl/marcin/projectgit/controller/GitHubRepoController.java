package pl.marcin.projectgit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import pl.marcin.projectgit.entity.UserGitRepo;
import pl.marcin.projectgit.service.GitHubApiService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class GitHubRepoController {
    private final GitHubApiService gitHubApiService;

//    @GetMapping("/repositories/{username}")
//    public ResponseEntity<List<?>> getRepositories(@PathVariable @Valid String username,
//                                                   @RequestHeader @Valid HttpHeaders headers) {
//        List<UserGitRepo> repositories = gitHubApiService.getUserRepositories(username, headers);
//        return ResponseEntity.ok(repositories);
//    }

    @GetMapping("/repositories/{username}")
    public ResponseEntity<List<?>> getRepositories(@PathVariable String username, @RequestHeader HttpHeaders headers) {
        try {
            if (!headers.getAccept().contains(MediaType.APPLICATION_JSON)) {
                String errorMessage = "Unsupported media type. Only 'application/json' is supported.";
                int statusCode = HttpStatus.NOT_ACCEPTABLE.value();
                String statusMessage = HttpStatus.NOT_ACCEPTABLE.getReasonPhrase();
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", statusCode);
                errorResponse.put("message", errorMessage);
                return ResponseEntity.status(statusCode).body(Collections.singletonList(errorResponse));
            }
            List<UserGitRepo> repositories = gitHubApiService.getUserRepositories(username, headers);
            return ResponseEntity.ok(repositories);
        } catch (HttpClientErrorException.NotFound exc) {
            String errorMessage = "User not found";
            int statusCode = exc.getStatusCode().value();
            String statusMessage = exc.getStatusText();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", statusCode);
            errorResponse.put("message", errorMessage);
            return ResponseEntity.status(statusCode).body(Collections.singletonList(errorResponse));
        } catch (RuntimeException exc) {
            String errorMessage = "Failed to get user Repositories form GitHub API";
            int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            String statusMessage = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", statusCode);
            errorResponse.put("message", errorMessage);
            return ResponseEntity.status(statusCode).body(Collections.singletonList(errorResponse));
        }
    }
}

