package com.example.purchase.config;

import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.context.annotation.*;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
@Import(BeanValidatorPluginsConfiguration.class)
public class PersistenceContext {

	@Value("${db.url}")
	private String url;

	@Value("${db.password}")
	private String password;

	@Value("${db.username}")
	private String username;

	@Value("${db.driver}")
	private String driver;

	@Value("${db.maximum-pool-size}")
	private String poolSize;

	@Bean(destroyMethod = "close")
	public DataSource dataSource() {

		if (System.getenv("MYSQL_DATABASE") != null && !System.getenv("MYSQL_DATABASE").equalsIgnoreCase("")) {
			url = System.getenv("MYSQL_DATABASE");
		}

		if (System.getenv("MYSQL_USER") != null && !System.getenv("MYSQL_USER").equalsIgnoreCase("")) {
			username = System.getenv("MYSQL_USER");
		}

		if (System.getenv("MYSQL_PASSWORD") != null && !System.getenv("MYSQL_PASSWORD").equalsIgnoreCase("")) {
			password = System.getenv("MYSQL_PASSWORD");
		}

		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setDriverClassName(driver);
		dataSource.setJdbcUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setMaximumPoolSize(Integer.parseInt(poolSize));
		return dataSource;
	}

	@Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean emf =
				new LocalContainerEntityManagerFactoryBean();
		emf.setPackagesToScan("com.example.purchase");
		emf.setDataSource(dataSource());
		emf.setPersistenceUnitName("hibernateSpring");
		emf.setJpaVendorAdapter(createJpaVendorAdapter());
		emf.setJpaProperties(createHibernateProperties());
		emf.afterPropertiesSet();
		return emf;
	}

	private Properties createHibernateProperties() {
		Properties properties = new Properties();
		properties.setProperty(
				"hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
		return properties;
	}


	private JpaVendorAdapter createJpaVendorAdapter() {
		return new HibernateJpaVendorAdapter();
	}

	@Bean
    PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		return new JpaTransactionManager(emf);
	}

//	@Bean
//	public Docket api() {
//		return new Docket(DocumentationType.SWAGGER_2)
//				.select()
//				.apis(RequestHandlerSelectors.any())
//				.paths(PathSelectors.any())
//				.build();
//	}

	@Bean
	public Flyway flyway() {
		return Flyway.configure().locations("classpath:db/migration").dataSource(dataSource()).load();
	}

	@Bean
	public FlywayMigrationInitializer flywayInitializer(Flyway flyway) {
		return new FlywayMigrationInitializer(flyway);
	}
}