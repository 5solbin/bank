# Junit Bank App

### Jpa LocalDateTime 자동으로 생성하는 법
- @EnableJpaAuditing (Main 클래스)
- @EntityListners(AuditingEntityListner.class) (Entity 클래스)

```java
    @CreatedDate // Insert
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate //Update
    @Column(nullable = false)
    private LocalDateTime updatedAt;
```
