FROM openjdk:17-jdk-alpine as builder
WORKDIR carSharingApp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} carSharingApp.jar
RUN java -Djarmode=layertools -jar carSharingApp.jar extract

FROM openjdk:17-jdk-alpine
WORKDIR carSharingApp
COPY --from=builder carSharingApp/dependencies/ ./
COPY --from=builder carSharingApp/spring-boot-loader/ ./
COPY --from=builder carSharingApp/snapshot-dependencies/ ./
COPY --from=builder carSharingApp/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
EXPOSE 8088
