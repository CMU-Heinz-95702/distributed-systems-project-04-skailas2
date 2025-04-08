# Use a Tomcat base image
FROM tomcat:10.1.0-M5-jdk16-openjdk-slim-bullseye

# Copy the prebuilt ROOT.war into Tomcat's webapps directory
COPY ROOT.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
