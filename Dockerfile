FROM navikt/java:11

COPY target/dependency/*.jar ./
COPY target/helse-spam.jar ./app.jar

