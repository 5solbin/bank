package shop.mtcoding.bank.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import shop.mtcoding.bank.config.jwt.JwtAuthenticationFilter;
import shop.mtcoding.bank.config.jwt.JwtAuthorizationFilter;
import shop.mtcoding.bank.domain.user.UserEnum;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.uti.CustomResponseUtil;


@Configuration
public class SecurityConfig {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Bean //IOC 컨테이너에 BCryptPasswordEncoder 객체가 등록 됨
    public BCryptPasswordEncoder passwordEncoder() {
        log.debug("디버그 : BCryptPasswordEncoder 빈 등록 됨");
        return new BCryptPasswordEncoder();
    }

    // JWT 필터 등록이 필요함
    public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
            builder.addFilter(new JwtAuthenticationFilter(authenticationManager));
            builder.addFilter(new JwtAuthorizationFilter(authenticationManager));
            super.configure(builder);
        }
    }

    //  JWT 서버를 만들 예정 !! Session 사용 안함
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.debug("디버그 : filterChain 빈 등록됨");

        http.headers().frameOptions().disable(); // iframe 허용 안함 **
        http.csrf().disable(); // enable 이면 post맨 작동 안함 **
        http.cors().configurationSource(configurationSource()); // 자바 스크립트 요청을 거부

        // jSessionId 를 서버쪽에서 관리 안하겠다는 뜻
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // react, 앱으로 요청할 예정
        http.formLogin().disable();
        // httpBasic은 브라우저가 팝업창을 이용해서 사용자 인증을 진행한다.
        http.httpBasic().disable();

        // 필터 적용
        http.apply(new CustomSecurityFilterManager());

        // 인증 실패
        http.exceptionHandling().authenticationEntryPoint((request, response, oauthException) -> {
            CustomResponseUtil.fail(response, "로그인을 진행해 주세요", HttpStatus.UNAUTHORIZED);
        });

        // 권한 실패
        http.exceptionHandling().accessDeniedHandler((request,response,e)->{
            CustomResponseUtil.fail(response,"권한이 없습니다.", HttpStatus.FORBIDDEN);
        });

        http.authorizeRequests()
                .antMatchers("/api/s/**").authenticated()
                .antMatchers("/api/admin/**").hasRole("" + UserEnum.ADMIN) // 최근 공식 문서에서는 ROLE_ 안붙여도 됨
                .anyRequest().permitAll();

        return http.build();
     }

     public CorsConfigurationSource configurationSource() {
         log.debug("디버그 : configurationSource cors 설정이 SecurityFilterChain에 등록됨");
         CorsConfiguration configuration = new CorsConfiguration();
         configuration.addAllowedHeader("*");
         configuration.addAllowedMethod("*"); // GET, POST, PUT, DELETE (Javascript 요청 허용)
         configuration.addAllowedOriginPattern("*"); // 모든 IP 주소 허용 (프론트 앤드 IP만 허용 react)
         configuration.setAllowCredentials(true); // 클라이언트에서 쿠키 요청 허용
         configuration.addExposedHeader("Authorization"); // 헤더에 authorization 값을 노출 시켜서 JS가 그 값을 가져올 수 있도록 함

         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
         source.registerCorsConfiguration("/**", configuration);
         return source;
     }
}
