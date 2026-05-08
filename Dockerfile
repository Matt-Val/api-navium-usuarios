# Usamos una versión ligera de Java 17
FROM eclipse-temurin:17-jdk-alpine

# Creamos una carpeta de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el archivo .jar que genera Maven hacia dentro del contenedor
COPY target/*.jar app.jar

# Le decimos a Docker que este contenedor usará el puerto 8085
EXPOSE 8085

# El comando que ejecutará al encender
ENTRYPOINT ["java", "-jar", "app.jar"]