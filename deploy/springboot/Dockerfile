FROM eclipse-temurin:17-jre

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone
ENV TZ=Asia/Shanghai

WORKDIR /app

COPY springboot/web.jar /app/springboot.jar

EXPOSE 8091

CMD ["java", "-Duser.timezone=Asia/Shanghai", "-jar", "springboot.jar"]