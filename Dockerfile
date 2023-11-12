FROM openjdk:17
WORKDIR /
ADD /release/unknownbot-1.0-jar-with-dependencies.jar /release/unknownbot-1.0-jar-with-dependencies.jar
EXPOSE 8080
EXPOSE 12102
CMD cd release && java -jar unknownbot-1.0-jar-with-dependencies.jar
