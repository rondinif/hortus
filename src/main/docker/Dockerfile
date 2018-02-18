FROM registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift
ENV JAVA_OPTIONS=-Dvertx.cacheDirBase=/tmp JAVA_APP_DIR=/deployments
EXPOSE 8080 8778 9779
COPY maven /deployments/
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTIONS -Drondinif.env=DEV -jar /deployments/hortus-1.0.0-SNAPSHOT.jar run com.wkbotanica.api.vertx.HttpApplication --conf /config/booster-rest-http.json" ]