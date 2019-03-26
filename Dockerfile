FROM centos:7.6.1810

USER root

RUN yum install -y java-1.8.0-openjdk ;\
yum clean all


# Set the JAVA_HOME variable to make it clear where Java is located
ENV JAVA_HOME /etc/alternatives/jre

RUN mkdir -p /app

EXPOSE 8080

COPY target/mission-service-1.0.0-SNAPSHOT.jar /app/

COPY run-java.sh /app/

RUN chmod 755 /app/run-java.sh

CMD [ "/app/run-java.sh" ]




