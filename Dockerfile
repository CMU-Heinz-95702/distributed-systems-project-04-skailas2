# Use a Linux image with Tomcat 10
FROM tomcat:10.1.0-M5-jdk16-openjdk-slim-bullseye

# Copy in our ROOT.war to the right place in the container
COPY ROOT.war /usr/local/tomcat/webapps/

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
