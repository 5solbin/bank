package shop.mtcoding.bank.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.dto.account.AccountReqDto;
import shop.mtcoding.bank.dto.account.AccountRespDto;
import shop.mtcoding.bank.handler.ex.CustomForbiddenException;
import shop.mtcoding.bank.service.AccountService;

import javax.validation.Valid;

import static shop.mtcoding.bank.dto.account.AccountReqDto.*;
import static shop.mtcoding.bank.dto.account.AccountRespDto.*;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/s/account")
    public ResponseEntity<?> saveAccount(@RequestBody @Valid AccountSaveReqDto accountSaveReqDto,
                                         BindingResult bindingResult, @AuthenticationPrincipal LoginUser loginUser) {
        AccountSaveResDto accountSaveResDto = accountService.계좌등록(accountSaveReqDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌등록 성공", accountSaveResDto), HttpStatus.CREATED);
    }

    // 인증이 필요하고, account 테이블에 1번 row를 주세요!!
    // cos로 로그인을 했는데, cos의 id가 2번이에요!!
    // 권한처리 때문에 선호하지 않는다.

    // 인증이 필요하고, account 테이블 데이터 다 주세요!!

    // 인증이 필요하고, account 테이블에 login한 유저의 계좌만 주세요
    @GetMapping("/s/account/login-user")
    public ResponseEntity<?> findUserAccount( @AuthenticationPrincipal LoginUser loginUser) {

        AccountListResDto accountListResDto = accountService.계좌목록보기_유저별(loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌목록보기_유저별 성공", accountListResDto), HttpStatus.OK);
    }

    @DeleteMapping("/s/account/{number}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long number, @AuthenticationPrincipal LoginUser loginUser) {
        accountService.계좌삭제(number,loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 삭제 완료", null), HttpStatus.OK);
    }

    @PostMapping("/account/deposit")
    public ResponseEntity<?> depositAccount(@RequestBody @Valid AccountDepositReqDto accountDepositReqDto
    ,BindingResult bindingResult) {
        AccountDepositResDto accountDepositResDto = accountService.계좌입금(accountDepositReqDto);
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 입금 완료", accountDepositResDto), HttpStatus.CREATED);
    }

    @PostMapping("/s/account/withdraw")
    public ResponseEntity<?> depositAccount(@RequestBody @Valid AccountWithdrawReqDto accountWithdrawReqDto
            ,BindingResult bindingResult, @AuthenticationPrincipal LoginUser loginUser) {
        AccountWithdrawResDto accountWithdrawResDto = accountService.계좌출금(accountWithdrawReqDto, loginUser.getUser().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 출금 완료", accountWithdrawReqDto), HttpStatus.CREATED);
    }

    @PostMapping("/s/account/transfer")
    public ResponseEntity<?> transferAccount(@RequestBody @Valid AccountTransferReqDto accountTransferReqDto
            ,BindingResult bindingResult, @AuthenticationPrincipal LoginUser loginUser) {
        AccountTransferResDto accountTransferResDto = accountService.계좌이체(accountTransferReqDto, loginUser.getUser().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 이체 완료", accountTransferReqDto), HttpStatus.CREATED);
    }

    @GetMapping("/s/account/{number}")
    public ResponseEntity<?> findDetailAccount(@PathVariable Long number, @AuthenticationPrincipal LoginUser loginUser,
                                               @RequestParam(value = "page", defaultValue = "0") Integer page) {

        AccountDetailResDto accountDetailResDto = accountService.계좌상세보기(number, loginUser.getUser().getId(), page);
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 상세보기 성공", accountDetailResDto), HttpStatus.OK);
    }


}
