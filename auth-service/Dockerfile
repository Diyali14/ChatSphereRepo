FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY . .
RUN mvn -pl auth-service -am clean package -DskipTests
RUN echo 'import java.net.InetAddress; \
public class DNSDebug { \
    public static void main(String[] args) { \
        String[] hosts = {"chatsphere-rabbitmq", "chatsphere-rabbitmq.render.internal", "discovery-server-rfh6", "discovery-server-rfh6.render.internal", "config-server-bk51", "config-server-bk51.render.internal"}; \
        for (String host : hosts) { \
            try { \
                InetAddress[] addrs = InetAddress.getAllByName(host); \
                System.out.println("DNS RESOLVE SUCCESS: " + host); \
                for (InetAddress addr : addrs) { \
                    System.out.println("  " + addr.getHostAddress()); \
                } \
            } catch (Exception e) { \
                System.out.println("DNS RESOLVE FAILED: " + host + " -> " + e.getMessage()); \
            } \
        } \
    } \
}' > /app/DNSDebug.java && javac /app/DNSDebug.java

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/auth-service/target/*.jar app.jar
COPY --from=build /app/DNSDebug.class /app/DNSDebug.class
EXPOSE 8081
ENTRYPOINT ["sh", "-c", "echo '--- RESOLV.CONF ---' && cat /etc/resolv.conf && echo '--- HOSTS ---' && cat /etc/hosts && echo '--- DNS RESOLVE TEST ---' && java -cp /app DNSDebug && java -XX:MaxRAMPercentage=75.0 -jar app.jar"]
