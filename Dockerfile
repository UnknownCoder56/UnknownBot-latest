FROM java:11
WORKDIR /target/
ADD HelloWorld.jar HelloWorld.jar
EXPOSE 8080
CMD java -jar unknownbot-1.0-jar-with-dependencies.jar
