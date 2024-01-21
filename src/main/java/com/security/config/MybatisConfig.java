package com.security.config;

import javax.sql.DataSource;


import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//※어노테이션방식이 아닌 XML방식으로 쿼리 실행시 XML매퍼 파일의 위치를 
//  등록해줘야 한다
// @MapperScan(value = {},sqlSessionFactoryRef ="" )
// 위 어노테이션은 @Mapper가 붙은 매퍼 인터페이스를 스캔한다
// value={매퍼 인터페이스들의 패키지 나열},sqlSessionFactoryRef는 SqlSessionFactory빈의 아이디 설정

@Configuration
@MapperScan(value = {"com.security.model"}, sqlSessionFactoryRef ="sqlSessionFactory" )
public class MybatisConfig {
	//https://mybatis.org/spring/ko/factorybean.html
	
	//생성자 인젝션을 통해 ApplicationContext를 컨테이너로 부터 받는다
	private final ApplicationContext applicationContext;
	public MybatisConfig(ApplicationContext applicationContext) {
		this.applicationContext=applicationContext;
	}
	
	@Bean
	SqlSessionFactory sqlSessionFactory(DataSource dataSource) {
		SqlSessionFactory factory=null;
		try {
			SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
			//데이타 소스 설정
			factoryBean.setDataSource(dataSource);
			//타입 별칭을 적용할 최상위 패키지 경로 설정
			//마이바티스 프레임워크는 최상위 패키지부터 하위패키지까지 @Alias붙은 컴포넌트를 찾는다
			factoryBean.setTypeAliasesPackage("com.security");
			//매퍼파일의 경로 설정
			factoryBean.setMapperLocations(applicationContext.getResources("classpath:mybatis/mapper/*.xml"));
			//SqlSessionFactoryBean의 getObject()로 SqlSessionFactory객체 얻기
			factory=factoryBean.getObject();
		
		}
		catch(Exception e) {e.printStackTrace();}
		return factory;
	}/////////////////
	
	@Bean
	SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
	
}
