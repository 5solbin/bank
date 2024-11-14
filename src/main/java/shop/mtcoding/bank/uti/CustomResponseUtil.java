package shop.mtcoding.bank.uti;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import shop.mtcoding.bank.dto.ResponseDto;

import javax.servlet.http.HttpServletResponse;

public class CustomResponseUtil {

    private static final Logger log = LoggerFactory.getLogger(CustomResponseUtil.class);

    public static void success(HttpServletResponse response, Object dto){

        try{
            ObjectMapper om = new ObjectMapper();
            ResponseDto<?> responseDto = new ResponseDto<>(1, "로그인성공", dto);
            String responseBody = om.writeValueAsString(responseDto);
            response.setContentType("application/jsion; charset=utf-8");
            response.setStatus(200);
            response.getWriter().println(responseBody); // 예쁘게 메시지를 포장한느 공통적인 응답 DTO를 만들어 보자!

        }catch (Exception e) {
            log.error("서버 파싱 에러");
        }
    }

    public static void fail(HttpServletResponse response, String msg, HttpStatus httpStatus){

        try{
            ObjectMapper om = new ObjectMapper();
            ResponseDto<?> responseDto = new ResponseDto<>(-1, msg, null);
            String responseBody = om.writeValueAsString(responseDto);
            response.setContentType("application/jsion; charset=utf-8");
            response.setStatus(httpStatus.value());
            response.getWriter().println(responseBody); // 예쁘게 메시지를 포장한느 공통적인 응답 DTO를 만들어 보자!

        }catch (Exception e) {
            log.error("서버 파싱 에러");
        }
    }

}
