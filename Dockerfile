FROM registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift:1.5
ENV JAVA_OPTIONS="-Dvertx.cacheDirBase=/tmp -Dvertx.disableDnsResolver=true" JAVA_APP_DIR=/deployments
EXPOSE 8080 8778 9779
COPY target/mission-service-1.0.0-SNAPSHOT.jar /deployments/


