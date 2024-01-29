package hellojpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn            // 단일 테이블 전략은 이 설정이 없어도 필수로 DTYPE이 생성됨
public class Item {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private int price;
}
