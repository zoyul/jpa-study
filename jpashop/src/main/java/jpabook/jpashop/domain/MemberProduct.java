package jpabook.jpashop.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

// 다대다 매핑의 경우 중간 테이블을 엔티티로 만들어라
@Entity
public class MemberProduct {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    private int count;
    private int price;
    private LocalDateTime orderDateTime;
}
