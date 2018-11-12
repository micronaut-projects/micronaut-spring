FROM gradle:jdk8 as builder
COPY --chown=gradle:gradle . /home/gradle/greeting-service
WORKDIR /home/gradle/greeting-service
RUN ./gradlew build --no-daemon

FROM oracle/graalvm-ce:1.0.0-rc8 as graalvm
COPY --from=builder /home/gradle/greeting-service/ /home/gradle/greeting-service/
WORKDIR /home/gradle/greeting-service
RUN java -cp build/libs/*-all.jar \
            io.micronaut.graal.reflect.GraalClassLoadingAnalyzer \
            reflect.json
RUN native-image --no-server \
                 --class-path /home/gradle/greeting-service/build/libs/*-all.jar \
    			 -H:ReflectionConfigurationFiles=/home/gradle/greeting-service/reflect.json \
    			 -H:EnableURLProtocols=http \
    			 -H:IncludeResources='logback.xml|application.yml|META-INF/services/*.*' \
    			 -H:+ReportUnsupportedElementsAtRuntime \
    			 -H:+AllowVMInspection \
    			 --rerun-class-initialization-at-runtime='sun.security.jca.JCAUtil$CachedSecureRandomHolder',javax.net.ssl.SSLContext \
    			 --delay-class-initialization-to-runtime=io.netty.handler.codec.http.HttpObjectEncoder,io.netty.handler.codec.http.websocketx.WebSocket00FrameEncoder,io.netty.handler.ssl.util.ThreadLocalInsecureRandom \
    			 -H:-UseServiceLoaderFeature \
    			 -H:Name=greeting-service \
    			 -H:Class=greeting.example.Application


FROM frolvlad/alpine-glibc
EXPOSE 8080
COPY --from=graalvm /home/gradle/greeting-service/greeting-service .
ENTRYPOINT ["./greeting-service"]
