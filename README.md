# file-ops
1.Create Git Repo 
2.Upload a file to git repo
3.Download file from git repo
4.Upload file to local disk
5.Download file from server


Java Spring-boot REST APIs available in this module with their signatures are below:

1.Create Git repository
{POST [/api/createGitRepo/{repoName}]}
PathVariable  String repoName,RequestParam String gitAuthToken

2.Upload a File to Git
{POST [/api/uploadFileToGit]}
RequestParam MultipartFile file, RequestParam String gitAuthToken

3.Download file from Git
{GET [/api/downloadFileFromGit/{filename:.+}]}
PathVariable String filename, RequestParam String gitAuthToken

4.Download file From Server
{GET [/api/downloadFileFromDropbox/]}
RequestParam String filepath

5.Upload the file and save to local disk
{POST [/api/uploadFileToLocalDisk]}:
@RequestParam MultipartFile file


To access Github APIS  ,access tokens needs to be passed as parameter per API signature.
The Dropbox is used for implemneting 'download from server' API .Access token used here needs to be given as user/application environment variable.
MockMVC Junit test cases for 5 APIs are included in FileOpsRestControllerTest.java class.
