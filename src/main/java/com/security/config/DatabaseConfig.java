package com.security.config;



import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


@Configuration
public class DatabaseConfig {
	
	//데이타 베이스 연결정보 주입
	@Value("${spring.datasource.driver-class-name}")
	private String driver;
	@Value("${spring.datasource.url}")
	private String url;
	@Value("${spring.datasource.username}")
	private String user;
	@Value("${spring.datasource.password}")
	private String password;
	
	@Bean
	DataSource dataSource() {
		//1.HikariConfig객체 생성후 데이타베이스 연결 및 커넥션 풀 정보 설정
		//JDBC연결 정보 설정
		System.out.println(user);
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setDriverClassName(driver);
		hikariConfig.setJdbcUrl(url);
		hikariConfig.setUsername(user);
		hikariConfig.setPassword(password);
		
		return new HikariDataSource(hikariConfig);
	}
	
	//트랜잭션 처리를 위한 빈 등록
	//트랜잭션 관리자 객체
	@Bean
	DataSourceTransactionManager transactionManager(HikariDataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}	
	//트랜잭션 관리자 객체를 TransactionTemplate의 트랜잭션 관리자로 설정
	//TransactionTemplate으로 서비스단에서 트랜잭션 처리한다
	@Bean
	TransactionTemplate transactionTemplate(DataSourceTransactionManager transactionManager) {
		TransactionTemplate transactionTemplate= new TransactionTemplate();
		transactionTemplate.setTransactionManager(transactionManager);
		return transactionTemplate;
	}
}
