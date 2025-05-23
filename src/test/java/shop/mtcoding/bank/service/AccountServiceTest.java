package shop.mtcoding.bank.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountReqDto;
import shop.mtcoding.bank.dto.account.AccountReqDto.AccountDepositReqDto;
import shop.mtcoding.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import shop.mtcoding.bank.dto.account.AccountRespDto;
import shop.mtcoding.bank.dto.account.AccountRespDto.AccountDepositResDto;
import shop.mtcoding.bank.dto.account.AccountRespDto.AccountSaveResDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static shop.mtcoding.bank.service.AccountService.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest extends DummyObject {

    @InjectMocks // 모든 Mock들이 InjectMocks 로 주입됨
    private AccountService accountService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;

    @Spy // 진짜 객체를 InjectMocks에 주입한다
    private ObjectMapper om;

    @Test
    public void 계좌등록_test() throws Exception {
        //given
        Long userId = 1L;

        AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto();
        accountSaveReqDto.setNumber(1111L);
        accountSaveReqDto.setPassword(1234L);

        //stub1
        User ssar = newMockUser(userId, "ssar", "쌀");
        when(userRepository.findById(any())).thenReturn(Optional.of(ssar));


        //stub2
        when(accountRepository.findByNumber(any())).thenReturn(Optional.empty());

        //stub3
        Account ssarAccount = newMockAccount(1L, 1111L, 1000L,ssar);
        when(accountRepository.save(any())).thenReturn(ssarAccount);

        //when
        AccountSaveResDto accountSaveResDto = accountService.계좌등록(accountSaveReqDto, userId);
        String responseBody = om.writeValueAsString(accountSaveResDto);
        System.out.println("테스트 : " + responseBody);

        //then
        assertThat(accountSaveResDto.getNumber()).isEqualTo(1111L);
    }

    @Test
    public void 게좌삭제_test() throws Exception{
        //given
        Long number = 1111L;
        Long userId = 2L;

        //stub
        User ssar = newMockUser(1L, "ssar", "쌀");
        Account ssarAccount = newMockAccount(1L, 1111L, 1000L, ssar);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(ssarAccount));

        //when

        //then
        assertThrows(CustomApiException.class, () -> accountService.계좌삭제(number, userId));

    }

    // Account -> balance 변경 됐는지
    // transaction -> balance 잘 기록됐는지
    @Test
    public void 계좌입금_test() throws Exception{
        //given
        AccountDepositReqDto accountDepositReqDto = new AccountDepositReqDto();
        accountDepositReqDto.setNumber(1111L);
        accountDepositReqDto.setAmount(100L);
        accountDepositReqDto.setGubun("DEPOSIT");
        accountDepositReqDto.setTel("01088887777");

        //stub1
        User ssar = newMockUser(1L, "ssar", "쌀");
        Account ssarAccount1 = newMockAccount(1L, 1111L, 1000L, ssar);
        when(accountRepository.findByNumber((any()))).thenReturn(Optional.of(ssarAccount1)); // service가 실행돼야 실행댐

        //stub2 (스텁이 진행될 때 마다 연관관된 객체는 새로 만들어서 주입하기 - 타이밍 때문에 꼬인다.)
        Account ssarAccount2 = newMockAccount(1L,1111L,1000L,ssar);
        Transaction transaction = newMockDepositTransaction(1L, ssarAccount2);
        when(transactionRepository.save(any())).thenReturn(transaction); // service가 실행돼야 실행댐 >>  중복의 가능성이 있으므로  스텁마다 다른 객체를 생성해 준ㄷㅏ.

        //when
        AccountDepositResDto accountDepositResDto = accountService.계좌입금(accountDepositReqDto);
        System.out.println("테스트 : 트랜잭션 입금계좌 잔액 : " + accountDepositResDto.getTransaction().getDepositAccountBalance());
        System.out.println("테스트 : 계좌쪽 잔액 : " + ssarAccount1.getBalance());

        //then
        assertThat(ssarAccount1.getBalance()).isEqualTo(1100L);
        assertThat(accountDepositResDto.getTransaction().getDepositAccountBalance()).isEqualTo(1100L);
    }

    @Test
    public void 계좌입금_test2() throws Exception{
        //given
        AccountDepositReqDto accountDepositReqDto = new AccountDepositReqDto();
        accountDepositReqDto.setNumber(1111L);
        accountDepositReqDto.setAmount(100L);
        accountDepositReqDto.setGubun("DEPOSIT");
        accountDepositReqDto.setTel("01088887777");

        //stub1
        User ssar = newMockUser(1L, "ssar", "쌀");
        Account ssarAccount1 = newMockAccount(1L, 1111L, 1000L, ssar);
        when(accountRepository.findByNumber((any()))).thenReturn(Optional.of(ssarAccount1));

        //stub2 - stub 끼리는 꼭 독립적으로 실행
        User ssar2 = newMockUser(1L, "ssar", "쌀");
        Account ssarAccount2 = newMockAccount(1L,1111L,1000L,ssar2);
        Transaction transaction = newMockDepositTransaction(1L, ssarAccount2);
        when(transactionRepository.save(any())).thenReturn(transaction);


        //when
        accountService.계좌입금(accountDepositReqDto);
        String responseBody = om.writeValueAsString(accountDepositReqDto);
        System.out.println("테스트 : " + responseBody);

        //then
        assertThat(ssarAccount1.getBalance()).isEqualTo(1100L);
    }

    // 서비스 테스트를 보여준 것은 기술적인 테크닉!!
    //  진짜 서비스를 테스트하고 싶으면, 내가 지금 무엇을 여기서 테스트해야할지 명확히 구분(책임 분리)
    // DTO를 만드는 책임 -> 서비스에 있지만
    // DB 관련된 것도 -> 서비스의 것이 아님
    // DB 관련된 것을 조회했을 떄, 그 값을 통해서 어떤 비즈니스 로직이 흘러가는 것이 있으면 -> stub으로 정의해서 테스트 해보면 된다.
    @Test
    public void 계좌입금_test3() throws Exception{
        //given
        Account account = newMockAccount(1L, 1111L, 1000L, null);
        Long amount = 100L;
        //when
        if (amount <= 0L){
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");
        }
        account.deposit(100L);

        //then
        assertThat(account.getBalance()).isEqualTo(1100L);
    }

    /*
     * DTO 같은 경우는 컨트롤러에서 테스트가 가능 하다
     */

    // 계좌 출금

    // 계좌 이체

    // 계좌목록보기_유저별_테스트

    // 계좌 상세보기_테스트

}