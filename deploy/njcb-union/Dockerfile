FROM eclipse-temurin:8-jre

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone
ENV TZ=Asia/Shanghai

WORKDIR /app

COPY njcb-union/backend.jar /app/springboot.jar

EXPOSE 8123

CMD ["java", "-Duser.timezone=Asia/Shanghai", "-jar", "springboot.jar", "--spring.profiles.active=uat"]