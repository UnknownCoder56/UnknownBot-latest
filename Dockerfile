FROM java:11
WORKDIR /target/
ADD unknownbot-1.0-jar-with-dependencies.jar unknownbot-1.0-jar-with-dependencies.jar
EXPOSE 8080
CMD java -jar unknownbot-1.0-jar-with-dependencies.jar
