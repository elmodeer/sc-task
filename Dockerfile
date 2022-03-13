FROM alpine:3.13.5

#install java
ENV JAVA_HOME /usr/lib/jvm/java-1.11-openjdk
ENV PATH $PATH:/usr/lib/jvm/java-1.11-openjdk/jre/bin:/usr/lib/jvm/java-1.11-openjdk/bin
ENV JAVA_ALPINE_VERSION 11.0.9_p11-r1
ENV BOOTAPP_PATH /opt/service/app.jar

RUN apk --no-cache add openjdk11-jdk="$JAVA_ALPINE_VERSION"

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} $BOOTAPP_PATH

ENTRYPOINT ["java","-jar","$BOOTAPP_PATH"]
