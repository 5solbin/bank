package shop.mtcoding.bank.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // jpa query method
    // select * from account where number = :number
    // 신경 안써도 댐 : 리팩토링 해야함 (계좌 소유자 확인시에 쿼리가 두번 나가기 때문에 join fetch)
    // 조인 fetch를 하면 조인에서 객체에 값을 미리 가져올 수 있다.
    @Query("SELECT ac FROM Account ac JOIN FETCH ac.user u WHERE ac.number = :number")
    Optional<Account> findByNumber(@Param("number") Long number);

    // jpa query method
    // select * from account where user_id = :id
    List<Account> findByUser_id(Long id);
}
