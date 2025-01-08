package study.datajpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {

    // jpa 식별자 생성 전략이 @GenerateValue 면 save() 호출 시점에 식별자 없으므로 새로운 엔티티로 인식하지만
    // @GenerateValue 를 사용하지 않고 직접 할당한다면, 식별자가 있는 상태로 save() 호출 -> JPA 내부적으로 merge 호출
    // merge : DB를 우선으로 호출 후 값을 확인 후 값이 없으면 새로운 엔티티로 인지 -> DB 호출이 한 번 더 추가되므로 비효율적
    // Persistable 의 isNew 메서드를 override 해서 새로운 엔티티 여부를 확인할 수 있도록 구현 (ex.@CreatedDate 등)
    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdDate;

    public Item(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}
