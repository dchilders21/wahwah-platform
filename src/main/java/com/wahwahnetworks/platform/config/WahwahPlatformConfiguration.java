package com.wahwahnetworks.platform.config;

import com.wahwahnetworks.platform.data.entities.enums.Environment;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.Log4JLogChute;
import org.flywaydb.core.Flyway;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jndi.JndiTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.mail.Session;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Created by Justin on 12/26/2014.
 */

@Configuration
@ComponentScan(basePackages = "com.wahwahnetworks.platform")
@EnableWebMvc
@Import({WebSocketConfiguration.class,RabbitConfiguration.class,AsyncConfiguration.class})
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories(basePackages = "com.wahwahnetworks.platform.data.repos")
@EnableAsync
@EnableScheduling
@EnableRabbit
public class WahwahPlatformConfiguration
{

	public WahwahPlatformConfiguration() {

    }

	@Bean
	public InternalResourceViewResolver internalResourceViewResolver()
	{
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/pages/");
		viewResolver.setSuffix(".jsp");
		return viewResolver;
	}

	@Bean(destroyMethod = "")
	public DataSource dataSource() throws NamingException
	{
		JndiTemplate jndiTemplate = new JndiTemplate();
		DataSource dataSource = (DataSource) jndiTemplate.lookup("java:comp/env/jdbc/WahwahPlatform");
		return dataSource;
	}

	@Bean(destroyMethod = "")
	public Session mailSession() throws NamingException
	{
		JndiTemplate jndiTemplate = new JndiTemplate();
		Session mailSession = (Session) jndiTemplate.lookup("java:comp/env/mail/WahwahPlatform");
		return mailSession;
	}

	@Bean
	public JavaMailSenderImpl mailSender(Session mailSession)
	{
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setSession(mailSession);
		return javaMailSender;
	}

	@Bean
	public Flyway flyway(DataSource dataSource)
	{
		Flyway flyway = new Flyway();
		flyway.setDataSource(dataSource);
		flyway.repair();
		flyway.migrate();
		return flyway;
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource, @SuppressWarnings("unused") Flyway flyway)
	{
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		return jdbcTemplate;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, @SuppressWarnings("unused") Flyway flyway)
	{
		LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		localContainerEntityManagerFactoryBean.setDataSource(dataSource);
		localContainerEntityManagerFactoryBean.setPackagesToScan("com.wahwahnetworks.platform.data");

		HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
		hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
		hibernateJpaVendorAdapter.setGenerateDdl(false);
		hibernateJpaVendorAdapter.setShowSql(true);

		localContainerEntityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
		localContainerEntityManagerFactoryBean.setJpaDialect(new HibernateJpaDialect());

		return localContainerEntityManagerFactoryBean;
	}

	@Bean
	public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactoryBean)
	{
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		jpaTransactionManager.setEntityManagerFactory(entityManagerFactoryBean);
		jpaTransactionManager.setJpaDialect(new HibernateJpaDialect());
		return jpaTransactionManager;
	}

	@Bean
	public VelocityEngine velocityEngine()
	{

		Properties properties = new Properties();
		properties.setProperty("resource.loader", "class");
		properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS, Log4JLogChute.class);
		velocityEngine.init(properties);

		return velocityEngine;
	}

	@Bean
	public MultipartResolver multipartResolver()
	{
		return new StandardServletMultipartResolver();
	}

	@Bean
	public Executor taskExecutor() {
		return new SimpleAsyncTaskExecutor();
	}

	@Bean(destroyMethod = "")
	public Environment environment()
	{
		try
		{
			JndiTemplate jndiTemplate = new JndiTemplate();
			Environment environment = Environment.valueOf((String) jndiTemplate.lookup("java:comp/env/environment_name"));
			return environment;
		}
		catch (Exception e)
		{
			return Environment.OTHER;
		}
	}
}
