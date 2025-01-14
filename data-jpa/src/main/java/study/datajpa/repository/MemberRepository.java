package study.datajpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

// interface를 선언하면 spring data jpa가 구현체를 직접 주입해줌
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // 기본 : NamedQuery가 우선순위 (그 다음이 쿼리메소드)
    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    // 복잡한 정적쿼리, 메소들 이름이 길어지는 쿼리 등에 추천
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findByUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    // 반환 타입을 유연하게 사용할 수 있다
    List<Member> findListByUsername(String username);
    Member findMemberByUsername(String username);
    Optional<Member> findOptionalByUsername(String username);

    // count 쿼리를 따로 분리할 수도 있다 쿼리가 복잡해 질 때 추천
//    @Query(value = "select m from Member m left join m.team t",
//        countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);
    Slice<Member> findSliceByAge(int age, Pageable pageable);

    // modifying 필수
    @Modifying
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus2(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    // entity graph (내부적으로 fetch join을 한다, 기본 left join)
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    // jpql과 EntityGraph 를 동시에 사용할수도 있다
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})
//    @EntityGraph("Member.all")            // 이렇게 Entity에 @NamedEntityGraph를 설정해서 사용할 수도 있다
    // 참고로 find...By 에서 ... 에는 아무 문자나 적어도 동일하다
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    // jpa에서 제공하는 lock을 애노테이션을 추가하여 편하게 쓸 수 있다
    @Lock(LockModeType.PESSIMISTIC_READ)
    List<Member> findLockByUsername(String username);

    List<UsernameOnly> findProjectionsByUsername(@Param("username") String username);

    <T> List<T>  findProjectionsByUsername(@Param("username") String username, Class<T> type);

    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    @Query(value = "select m.member_id as id, m.username, t.name as teamName " +
            "from member m left join team t",
            countQuery = "select count(*) from member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
