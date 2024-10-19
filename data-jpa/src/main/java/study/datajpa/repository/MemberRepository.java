package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

// interface를 선언하면 spring data jpa가 구현체를 직접 주입해줌
public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // 기본 : NamedQuery가 우선순위 (그 다음이 쿼리메소드)
    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);
}
