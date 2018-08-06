OPTIONS:

1] Execution

mvn package

java -jar target/materialsdbsvc-1.0-jar-with-dependencies.jar


2] Tests

mvn test

mvn test -Dtest=ControllerTest

3] Deploy

deploy with:

git push heroku master

mvn heroku:deploy


