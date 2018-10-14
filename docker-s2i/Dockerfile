FROM openjdk:8-jdk-alpine

MAINTAINER Martin Rumanek <martin@rumanek.cz>

LABEL io.openshift.s2i.scripts-url="image:///usr/libexec/s2i"
RUN apk add gradle bash

COPY ["run", "assemble", "usage", "/usr/libexec/s2i/"]

#ENTRYPOINT ["java",","-jar","/app.jar"]
