package shop.mtcoding.bank.domain.transaction;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import shop.mtcoding.bank.domain.account.Account;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor // 스프링이 User 객체를 생성할 때 빈 생성자로 new를 하기 떄문
@Getter
@EntityListeners(AuditingEntityListener.class) // createAt 이 작동을 하게 된스
@Entity
@Table(name = "transaction_tb")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    private Account withdrawAccount;
    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    private Account depositAccount;

    private Long amount;

    private Long withdrawAccountBalance; // 1111 계좌 -> 1000원 -> 500원 -> 200월
    private Long depositAccountBalance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionEnum gubun; // WITHDRAW, DEPOSIT , TRANSFER, ALL

    // 계좌가 사라져도 로그는 남아야 한다.
    private String sender;
    private String receiver;
    private String tel;

    @CreatedDate //Insert
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate //Update
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Transaction(Long id, Account withdrawAccount, Account depositAccount, Long amount,
                       Long withdrawAccountBalance, Long depositAccountBalance, TransactionEnum gubun, String sender, String receiver, String tel, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.withdrawAccount = withdrawAccount;
        this.depositAccount = depositAccount;
        this.amount = amount;
        this.withdrawAccountBalance = withdrawAccountBalance;
        this.depositAccountBalance = depositAccountBalance;
        this.gubun = gubun;
        this.sender = sender;
        this.receiver = receiver;
        this.tel = tel;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
