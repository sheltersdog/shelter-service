FROM openjdk:17-jdk AS builder
WORKDIR application
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} build/application.jar
RUN java -Djarmode=layertools -jar build/application.jar extract

FROM openjdk:17-jdk
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]