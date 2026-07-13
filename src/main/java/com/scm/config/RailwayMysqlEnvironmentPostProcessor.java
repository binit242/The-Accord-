package com.scm.config;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

public class RailwayMysqlEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String PROPERTY_SOURCE_NAME = "railwayMysqlPublicUrl";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String mysqlPublicUrl = firstNonBlank(
                environment.getProperty("MYSQL_PUBLIC_URL"),
                environment.getProperty("MYSQL_URL"));

        if (!StringUtils.hasText(mysqlPublicUrl)) {
            return;
        }

        try {
            URI uri = URI.create(mysqlPublicUrl.trim());
            if (!"mysql".equalsIgnoreCase(uri.getScheme()) || !StringUtils.hasText(uri.getHost())) {
                return;
            }

            String database = uri.getPath() == null ? "" : uri.getPath().replaceFirst("^/", "");
            if (!StringUtils.hasText(database)) {
                database = firstNonBlank(environment.getProperty("MYSQLDATABASE"), environment.getProperty("MYSQL_DB"), "railway");
            }

            int port = uri.getPort() > 0 ? uri.getPort() : 3306;
            String jdbcUrl = "jdbc:mysql://" + uri.getHost() + ":" + port + "/" + database
                    + "?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

            Map<String, Object> properties = new HashMap<>();
            properties.put("spring.datasource.url", jdbcUrl);

            String userInfo = uri.getRawUserInfo();
            if (StringUtils.hasText(userInfo)) {
                String[] parts = userInfo.split(":", 2);
                properties.put("spring.datasource.username", decode(parts[0]));
                if (parts.length > 1) {
                    properties.put("spring.datasource.password", decode(parts[1]));
                }
            }

            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
        } catch (IllegalArgumentException ignored) {
            // Let the normal datasource settings fail with a clear Spring Boot startup error.
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return "";
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}