# projectgithubrepo
program will fetch repositories from github when given username and header Accept: application/json
Project is using GitHub API. 
As an api consumer, given username and header “Accept: application/json”.
program lists all users github repositories, which are not forks. 
Information in the response, is:
Repository Name
Owner Login
For each branch it’s name and last commit sha

If user doesn't exist: it is shown 404 response in such a format:
{
    “status”: ${responseCode}
    “Message”: ${whyHasItHappened}
}
response is shown on (http://localhost:8080/repositories/username) when program is running. 
