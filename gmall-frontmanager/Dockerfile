FROM  registry.cn-hangzhou.aliyuncs.com/6well/lc-jdk17:17.2
ENV APP_HOME /home/admin/app
## jvm自定义参数注入
ENV JAVA_OPTS="$JAVA_OPTS"

ENV TZ=Asia/Shanghai
ENV SPRING_PROFILES_ACTIVE=dev

ARG JAR_FILE=front-bootstrap/target/front-bootstrap-1.1.0.fu-SNAPSHOT.jar
COPY ${JAR_FILE} ${APP_HOME}/app.jar

# 【固定】 启动应用
WORKDIR /home/admin
ENTRYPOINT java \
    -Xms2048m -Xmx2048m -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8787,suspend=n -Dedas.dubbo.subscribe.time=20  \
    -XX:-OmitStackTraceInFastThrow \
    --add-opens java.base/java.nio=ALL-UNNAMED \
    --add-opens java.base/java.lang=ALL-UNNAMED \
    --add-opens java.base/java.math=ALL-UNNAMED \
    --add-opens java.base/java.util=ALL-UNNAMED \
    --add-opens java.base/java.reflect=ALL-UNNAMED \
    --add-opens java.base/java.internal=ALL-UNNAMED \
    --add-opens java.base/java.util.concurrent=ALL-UNNAMED \
    --add-opens java.base/java.net=ALL-UNNAMED \
    --add-opens java.base/java.text=ALL-UNNAMED \
    --add-opens java.base/sun.reflect.annotation=ALL-UNNAMED \
    -jar ${APP_HOME}/app.jar
