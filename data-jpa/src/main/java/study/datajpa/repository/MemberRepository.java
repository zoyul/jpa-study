package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

// interface를 선언하면 spring data jpa가 구현체를 직접 주입해줌
public interface MemberRepository extends JpaRepository<Member, Long> {
}
