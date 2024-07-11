# Palindrome-Checker

## Instructions for running the application
To run the app there are 3 options:
- Run from intellij (or other IDE)
- Execute: mvn clean spring-boot:run
- Run the packaged jar found in the releases of this repo

**Please note - the application runs on port 7007**

Swagger documentation is available from the following address when the application is running: http://localhost:7007/swagger-ui/index.html

## Instructions for building the application
The application is a Spring Boot app. The following is required to be installed on the build machine:
- Java 11
- Maven verion 3.x

To build the application locally, execute the command:
- mvn clean package

If for any reason the maven cannot download any dependent jar, the full maven repo has been zipped and available in the "repository.zip" file.

## Assumptions
- Results are not to be stored per user
- Username will not be supplied in the body in the future if authication was added - it would be extracted for OAuth token
- A single character is a palindrome
- Zero character length input text should be rejected 
- Case sensitivity is ignored when determining if input text is a palindrome
- The username should only be alphanumeric
- No accented characters are permitted in the username
- The username should be a maximum of 25 characters
- The text value should be a maximum of 50 characters
- Accented characters (e.g. ć, é, ö) are permitted in the input text
- Accented characters will be treated as NOT equalling their none accented version. i.e. á is not equal to a.
- Special characters (e.g. £, $, &) are permitted in the input text
