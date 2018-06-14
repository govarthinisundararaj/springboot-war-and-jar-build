# springboot-war-and-jar-build
Basic scaffolding project for Spring Boot which builds both a WAR and a JAR.

Features
* Setup to use environment property files

To register a DataSource to use JNDI, add the following in Application.java. This is used for consistency between the 
two builds. It registers the DataSource as a Spring bean for bean management. I have not specified a destroyMethod 
since it should persists through the application's lifetime. I set the profile to all but test since I mock my unit 
tests (I connect externally only when performing integration tests). To use a DataSource during testing, just remove that line.

```java
@Bean(destroyMethod = "")
@Profile("!test")
public DataSource jndiDataSource() throws NamingException {
    JndiObjectFactoryBean jndiFactoryBean = new JndiObjectFactoryBean();
    jndiFactoryBean.setJndiName("%JNDI NAME%");
    jndiFactoryBean.setProxyInterface(DataSource.class);
    jndiFactoryBean.setLookupOnStartup(true);
    jndiFactoryBean.afterPropertiesSet();
    return (DataSource) jndiFactoryBean.getObject();
}
```
