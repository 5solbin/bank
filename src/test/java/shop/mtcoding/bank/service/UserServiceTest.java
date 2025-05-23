package shop.mtcoding.bank.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.user.UserReqDto.JoinReqDto;
import shop.mtcoding.bank.dto.user.UserRespDto.JoinRespDto;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// Spring 관련 Bean들이 하나도 없는 환경!!
@ExtendWith(MockitoExtension.class)
class UserServiceTest extends DummyObject {

    @InjectMocks // 가짜 환경 @Autowired x
    private UserService userService;

    @Mock // 가짜 환경 조성 (가짜를 넣고 싶을 때)
    private UserRepository userRepository;

    @Spy // 진짜를 가짜로 집어넣는다 (진짜를 넣고 싶을때)
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void 회원가입_test() throws Exception{
        //given
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setUsername("ssar");
        joinReqDto.setPassword("1234");
        joinReqDto.setEmail("ssal@nate.com");
        joinReqDto.setFullname("쌀");

        // stub1 -> 가설 같은거 (이런게 실행되면 이렇게 된다)
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        // stub 2

        User ssar = newMockUser(1L, "ssar", "쌀");
        when(userRepository.save(any())).thenReturn(ssar);

        //when
        JoinRespDto joinRespDto = userService.회원가입(joinReqDto);
        System.out.println("테스트 : " + joinRespDto);


        //then
        Assertions.assertThat(joinRespDto.getId()).isEqualTo(1L);
        Assertions.assertThat(joinRespDto.getUsername()).isEqualTo("ssar");


    }


}