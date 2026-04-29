package egovframework.com.config;

import java.io.IOException;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.support.lob.DefaultLobHandler;

/**
 * @ClassName : EgovConfigAppMapper.java
 * @Description : Mapper 설정
 *
 * @author : 윤주호
 * @since  : 2021. 7. 20
 * @version : 1.0
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일              수정자               수정내용
 *  -------------  ------------   ---------------------
 *   2021. 7. 20    윤주호               최초 생성
 * </pre>
 *
 */
@Configuration
@PropertySources({
	@PropertySource("classpath:/application.properties"),
	@PropertySource(value = "classpath:/application.yml", ignoreResourceNotFound = true, factory = YamlPropertySourceFactory.class)
})
public class EgovConfigAppMapper {

    private final DataSource dataSource;

    private final Environment env;

    public EgovConfigAppMapper(DataSource dataSource, Environment env) {
        this.dataSource = dataSource;
        this.env = env;
    }

	private String dbType;

	@PostConstruct
	void init() {
		dbType = env.getProperty("Globals.DbType");
	}

	@Bean
	@Lazy
	public DefaultLobHandler lobHandler() {
		return new DefaultLobHandler();
	}

	@Bean(name = {"sqlSession", "egov.sqlSession"})
	public SqlSessionFactoryBean sqlSession() {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);

		PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();

		sqlSessionFactoryBean.setConfigLocation(
			pathMatchingResourcePatternResolver
				.getResource("classpath:/egovframework/mapper/config/mapper-config.xml"));

		// Spring Boot fat JAR에서 MyBatis <package> 스캔이 동작하지 않으므로
		// Spring의 PathMatchingResourcePatternResolver를 사용하는 방식으로 패키지 등록
		sqlSessionFactoryBean.setTypeAliasesPackage(
			"arami.common.adminWeb.member.service.dto.request," +
			"arami.common.adminWeb.member.service.dto.response," +
			"arami.common.adminWeb.code.service.dto.request," +
			"arami.common.adminWeb.code.service.dto.response," +
			"arami.common.auth.service.dto," +
			"arami.adminWeb.artprom.service.dto.request," +
			"arami.adminWeb.artprom.service.dto.response," +
			"arami.adminWeb.artappm.service.dto.request," +
			"arami.adminWeb.artappm.service.dto.response," +
			"arami.adminWeb.artapps.service.dto.request," +
			"arami.adminWeb.artapps.service.dto.response," +
			"arami.adminWeb.artchoi.service.dto.request," +
			"arami.adminWeb.artchoi.service.dto.response," +
			"arami.adminWeb.artedum.service.dto.request," +
			"arami.adminWeb.artedum.service.dto.response," +
			"arami.adminWeb.artadvi.service.dto.request," +
			"arami.adminWeb.artadvi.service.dto.response," +
			"arami.adminWeb.armchil.service.dto.response," +
			"arami.adminWeb.armbuild.service.dto.request," +
			"arami.adminWeb.armbuild.service.dto.response," +
			"arami.adminWeb.banner.service.dto.request," +
			"arami.adminWeb.banner.service.dto.response," +
			"arami.adminWeb.support.service.dto.request," +
			"arami.adminWeb.support.service.dto.response," +
			"arami.shared.armuser.dto.request," +
			"arami.shared.armuser.dto.response," +
			"arami.shared.armchil.dto.request," +
			"arami.shared.armchil.dto.response," +
			"arami.shared.proc.dto.request," +
			"arami.shared.proc.dto.response," +
			"arami.shared.neis.dto.response," +
			"arami.member.dto," +
			"arami.userWeb.artprom.service.dto.request," +
			"arami.userWeb.artprom.service.dto.response," +
			"arami.userWeb.artappm.dto," +
			"arami.userWeb.article.service.dto.response," +
			"arami.userWeb.mentorWork.service.dto.request," +
			"arami.userWeb.mentorWork.service.dto.response," +
			"arami.userWeb.oauth.service.dto.request," +
			"arami.userWeb.oauth.service.dto.response"
		);

		try {
			sqlSessionFactoryBean.setMapperLocations(
				pathMatchingResourcePatternResolver
					.getResources("classpath:/egovframework/mapper/**/*_" + dbType + ".xml"));
		} catch (IOException e) {
			// TODO Exception 처리 필요
		}

		return sqlSessionFactoryBean;
	}

	@Bean
	public SqlSessionTemplate egovSqlSessionTemplate(@Qualifier("sqlSession") SqlSessionFactory sqlSession) {
		SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSession);
		return sqlSessionTemplate;
	}
}