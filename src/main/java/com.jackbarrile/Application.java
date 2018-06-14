package com.jackbarrile;

import org.apache.catalina.startup.Tomcat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = {"classpath:application-${spring.profiles.active}.properties"}, ignoreResourceNotFound = true)
public class Application extends SpringBootServletInitializer {

    @Value("${spring.datasource.tomcat.url:default}")
    String url;
    @Value("${spring.datasource.tomcat.username:default}")
    String username;
    @Value("${spring.datasource.tomcat.password:default}")
    String password;
    @Value("${spring.datasource.tomcat.driver-class-name:default}")
    String driverClassName;
    @Value("${spring.datasource.tomcat.initial-size:default}")
    String initialSize;
    @Value("${spring.datasource.tomcat.max-wait:default}")
    String maxWait;
    @Value("${spring.datasource.tomcat.max-active:default}")
    String maxActive;
    @Value("${spring.datasource.tomcat.max-idle:default}")
    String maxIdle;
    @Value("${spring.datasource.tomcat.max-age:default}")
    String maxAge;
    @Value("${spring.datasource.tomcat.test-on-borrow:default}")
    String testOnBorrow;
    @Value("${spring.datasource.tomcat.validation-query:default}")
    String validationQuery;
    @Value("${spring.profiles.active}")
    String activeSpringProfile;
    private String jndiName = null;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * This method is used by the SpringBootServletInitializer and is required to run on standalone Tomcat
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    /**
     * This factory creates an embedded Tomcat in a similar manner that a standalone Tomcat would get created. A
     * majority of this configuration is JNDI related. This code block only registers the embedded Tomcat with one
     * JNDI - it will have to be modified, along with appropriate properties, in order to handle multiple JNDI lookups
     *
     * @return TomcatEmbeddedServletContainerFactory - the factory that creates an instance of an embedded Tomcat
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.tomcat")
    @Profile({"dev", "test"})
    public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() {
        return new TomcatEmbeddedServletContainerFactory() {

            @Override
            protected TomcatEmbeddedServletContainer getTomcatEmbeddedServletContainer(Tomcat tomcat) {
                tomcat.enableNaming();
                return super.getTomcatEmbeddedServletContainer(tomcat);
            }
        };
    }

}