package hellojpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter @Setter
@DiscriminatorValue("A")            // DTYPE에 들어가는 값을 설정할 수 있음
public class Album extends Item {

    private String artist;
}
