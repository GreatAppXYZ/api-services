echo "::::::::::STARTING API SERVICES:::::::::::"
cd %~dp0
call gradlew build -x test
call java -jar build/libs/api-services-0.0.1-SNAPSHOT.jar
