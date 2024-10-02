FROM openjdk:17

WORKDIR /app

RUN ln -fs /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

COPY springboot/web.jar /app/springboot.jar

EXPOSE 8081

CMD ["java", "-jar", "springboot.jar"]