FROM openjdk:11
#todo add maven plugin for automatic docker build
ENV STUB_FILE_LOCATION=file:/stub.json
ENV REPLY_FILE_LOCATION=/replies
#Copy Jar
#todo remove version name from docker file
COPY target/replier-0.0.1-SNAPSHOT.jar replier.jar
#Copy Stub Json
COPY BUM/stub.json stub.json
# Copy Replies
COPY replies replies/

ENTRYPOINT ["java","-jar","/replier.jar"]