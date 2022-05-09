FROM openjdk:11
WORKDIR /
ADD /target/unknownbot-1.0-jar-with-dependencies.jar /target/unknownbot-1.0-jar-with-dependencies.jar
EXPOSE 8080
CMD java -jar /target/unknownbot-1.0-jar-with-dependencies.jar
