package shop.mtcoding.bank.domain.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

interface Dao {
    List<Transaction> findTransactionList(@Param("accountId") Long accountId, @Param("gubun") String gubun
            , @Param("page") Integer page);
}
// impl은 꼭 붙여줘야 하고 TransactionRepository 가 앞에 붙어야 한다.
// JpaRepository를 통해서 만들어 줘도 되지만 동적 쿼리를 만들기가 어려워서 여기다가 따로 빼둔다
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements Dao{
    private final EntityManager em;

    // JPQL이 먼지
    // join fetch
    // outer 조인을 해야하는지
    @Override
    public List<Transaction> findTransactionList(Long accountId, String gubun, Integer page) {
        // 동적 쿼리 (gubun 값을 가지고 동적쿼리 = DEPOSIT , WITHDRAW, ALL)

        //jpql
        String sql = "";
        sql += "select t from Transaction t ";

        if (gubun.equals("WITHDRAW")) {
            sql += "join fetch t.withdrawAccount wa ";
            sql += "where t.withdrawAccount.id = :withdrawAccountId";
        } else if (gubun.equals("DEPOSIT")) {
            sql += "join fetch t.depositAccount da ";
            sql += "where t.depositAccount.id = :depositAccountId";
        } else{ // gubun = ALL,
            sql += "left join t.withdrawAccount wa ";
            sql += "left join t.depositAccount da ";
            sql += "where t.withdrawAccount.id = :withdrawAccountId ";
            sql += "or ";
            sql += "t.depositAccount.id = :depositAccountId";
        }

        TypedQuery<Transaction> query = em.createQuery(sql, Transaction.class);

        if (gubun.equals("WITHDRAW")) {
            query = query.setParameter("withdrawAccountId", accountId);
        } else if (gubun.equals("DEPOSIT")) {
            query = query.setParameter("depositAccountId", accountId);
        } else {
            query = query.setParameter("withdrawAccountId", accountId);
            query = query.setParameter("depositAccountId", accountId);
        }

        query.setFirstResult(page*5); // 5, 10 ,15
        query.setMaxResults(5);

        return query.getResultList();

    }
}
