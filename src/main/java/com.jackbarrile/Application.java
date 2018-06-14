package com.jackbarrile;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
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
import org.springframework.jndi.JndiObjectFactoryBean;

import javax.activation.DataSource;
import javax.naming.NamingException;

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
        SpringApplication.run(Application.class);
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
    @Profile({"loc", "test"})
    public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() {
        return new TomcatEmbeddedServletContainerFactory() {

            @Override
            protected TomcatEmbeddedServletContainer getTomcatEmbeddedServletContainer(Tomcat tomcat) {
                tomcat.enableNaming();
                return super.getTomcatEmbeddedServletContainer(tomcat);
            }

            @Override
            protected void postProcessContext(Context context) {
                ContextResource contextResource = new ContextResource();
                contextResource.setName(jndiName);
                contextResource.setAuth("Container");
                contextResource.setType("javax.sql.DataSource");
                contextResource.setProperty("url", url);
                contextResource.setProperty("username", username);
                contextResource.setProperty("password", password);
                contextResource.setProperty("initialSize", initialSize);
                contextResource.setProperty("maxWaitMIllis", maxWait);
                contextResource.setProperty("maxTotal", maxActive);
                contextResource.setProperty("maxIdle", maxIdle);
                contextResource.setProperty("maxAge", maxAge);
                contextResource.setProperty("testOnBorrow", testOnBorrow);
                contextResource.setProperty("validationQuery", validationQuery);
                context.getNamingResources().addResource(contextResource);

            }
        };
    }

    /**
     * Registers a DataSource as a JNDI lookup (opposed to any other method of DataSource defining Spring boot offers).
     * Used for consistency since JNDI is usually configured for DataSources in a standalone Tomcat.
     */
    @Bean(destroyMethod = "")
    @Profile("!test")
    public DataSource jndiDataSource() throws NamingException {
        JndiObjectFactoryBean jndiFactoryBean = new JndiObjectFactoryBean();
        jndiFactoryBean.setJndiName("java:comp/env/" + jndiName);
        jndiFactoryBean.setProxyInterface(DataSource.class);
        jndiFactoryBean.setLookupOnStartup(true);
        jndiFactoryBean.afterPropertiesSet();
        return (DataSource) jndiFactoryBean.getObject();
    }

}