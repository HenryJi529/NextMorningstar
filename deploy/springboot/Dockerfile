# Debian11
FROM openjdk:17-ea-jdk

WORKDIR /app

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone
ENV TZ Asia/Shanghai

COPY springboot/web.jar /app/springboot.jar

EXPOSE 8081

CMD ["java", "-Duser.timezone=Asia/Shanghai", "-jar", "springboot.jar"]