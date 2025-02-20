package shop.mtcoding.bank.domain.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor // 스프링이 User 객체를 생성할 때 빈 생성자로 new를 하기 떄문
@Getter
@EntityListeners(AuditingEntityListener.class) // createAt 이 작동을 하게 된다
@Entity
@Table(name = "user_tb")
public class User { // extends (BaseTimeEntity) 시간설정(상속) >> Junit 테스트시에 불편한 점이 발생

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, length = 20)
    private String username;
    @Column(nullable = false, length = 60) //패스워드 인코딩(BCrypt) 하면 길이가 늘어나기 때문에 60자
    private String password;
    @Column(nullable = false, length = 20)
    private String email;
    @Column(nullable = false, length = 20)
    private String fullname;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserEnum role; //ADMIN, CUSTOMER

    @CreatedDate //Insert
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate //Insert, Update
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public User(Long id, String username, String password, String email, String fullname,
                UserEnum role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullname = fullname;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
