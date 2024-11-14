package shop.mtcoding.bank.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.dto.user.UserReqDto;
import shop.mtcoding.bank.dto.user.UserReqDto.LoginReqDto;
import shop.mtcoding.bank.dto.user.UserRespDto;
import shop.mtcoding.bank.dto.user.UserRespDto.LoginResDto;
import shop.mtcoding.bank.uti.CustomResponseUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        setFilterProcessesUrl("/api/login");
        this.authenticationManager = authenticationManager;
    }

    // Post : /login
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.debug("디버그 : attemptAuthentication 호출됨");
            try {
                ObjectMapper om = new ObjectMapper();
                LoginReqDto loginReqDto = om.readValue(request.getInputStream(), LoginReqDto.class);

                // 강제 로그인
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        loginReqDto.getUsername(), loginReqDto.getPassword()
                );

                // UserDetailService 의 loadUserByUsername 호출
                // Jwt를 쓴다고 하더라도, 컨트롤러에 진입을 하게 되면 시큐리티의 권한 체크, 인증체크의 도움을 받을 수 있게 세션을 만든다.
                // 이세션의 유효기간은 request하고, response하면 끝!!
                Authentication authentication = authenticationManager.authenticate(authenticationToken);
                return authentication;
            } catch (Exception e) {
                // unsuccessfulAuthentication 호출함
                throw new InternalAuthenticationServiceException(e.getMessage());
            }
    }

    // 로그인 실패
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        CustomResponseUtil.fail(response, "로그인실패", HttpStatus.UNAUTHORIZED);
    }

    // return authentication 잘 작동하면 successfulAuthentiacation 메서드 호출됩니다.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.debug("디버그 : successfulAuthentication 호출됨");
        LoginUser loginUser = (LoginUser) authResult.getPrincipal();
        String jwtToken = JwtProcess.create(loginUser);
        response.addHeader(JwtVo.HEADER, jwtToken);

        LoginResDto loginResDto = new LoginResDto(loginUser.getUser());
        CustomResponseUtil.success(response,loginResDto);

    }
}
