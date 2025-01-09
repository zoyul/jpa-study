package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);           // 같은 transaction 안에서는 같은 영속성 컨텍스트의 데이터임을 보장한다
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(10);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(10);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findByUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> result = memberRepository.findByUsernameList();

        assertThat(result.get(0)).isEqualTo("AAA");
        assertThat(result.get(1)).isEqualTo("BBB");
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);
        Member m1 = new Member("AAA", 10, team);
        memberRepository.save(m1);

        List<MemberDto> result = memberRepository.findMemberDto();

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getTeamName()).isEqualTo("teamA");
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(List.of("AAA", "BBB"));

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> listResult = memberRepository.findListByUsername("AAA");
        Member memberResult = memberRepository.findMemberByUsername("AAA");
        Optional<Member> optionalResult = memberRepository.findOptionalByUsername("AAA");

        assertThat(listResult.size()).isEqualTo(1);
        assertThat(memberResult.getUsername()).isEqualTo("AAA");
        assertThat(optionalResult.get().getUsername()).isEqualTo("AAA");
    }

    @Test
    public void returnType2() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> listResult = memberRepository.findListByUsername("ZZZ");
        Member memberResult = memberRepository.findMemberByUsername("ZZZ");
        Optional<Member> optionalResult = memberRepository.findOptionalByUsername("ZZZ");

        // 반환타입이 list인 경우 empty list 반환(not null), 객체로 받을 경우 null을 반환
        assertThat(listResult.size()).isEqualTo(0);
        assertThat(memberResult).isNull();
        assertThat(optionalResult).isEmpty();
    }

    @Test
    public void paging() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by( Sort.Direction.DESC, "username"));

        // pageable 구현체를 넘겨주면 됨
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        // page는 map을 사용해서 dto로 쉽게 변환할 수 있음
        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        assertThat(page.getContent().size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void pagingUsingSlice() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by( Sort.Direction.DESC, "username"));

        // slice는 count를 받아오지 않고 limit를 + 1 한 값으로 가져옴
        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);

        assertThat(page.getContent().size()).isEqualTo(3);
//        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
//        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkAgePlus() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 22));
        memberRepository.save(new Member("member5", 40));

        // bulk update를 실행하면 영속성 컨텍스트를 무시하고 바로 db에 반영한다
        int resultCount = memberRepository.bulkAgePlus(20);
        assertThat(resultCount).isEqualTo(3);

        // 영속성 컨텍스트에서 찾아오기 떄문에 db에 반영되지 않은 1차 캐시 값이 반환된다 이미 db에는 업데이트가 되었음에도
        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        assertThat(member5.getAge()).isEqualTo(40);

        // 영속성 컨텍스트를 초기화 한 후 조회해야함
        em.flush();
        em.clear();
        List<Member> result2 = memberRepository.findByUsername("member5");
        Member member5_2 = result2.get(0);
        assertThat(member5_2.getAge()).isEqualTo(41);
    }

    @Test
    public void bulkAgePlus2() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 22));
        memberRepository.save(new Member("member5", 40));

        // clearAutomatically = true 설정 시 clear를 자동으로 해줌
        int resultCount = memberRepository.bulkAgePlus2(20);
        assertThat(resultCount).isEqualTo(3);

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        assertThat(member5.getAge()).isEqualTo(41);
    }

    @Test
    public void findMemberLazy() {
        // given
        // member1 -> teamA
        // member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // N + 1 문제 발생 (entity graph 사용 전)
        List<Member> members = memberRepository.findAll();
        members.forEach(m -> {
            System.out.println("member = " + m.getUsername());
            // class study.datajpa.entity.Team$HibernateProxy$cTCYt8eY
            System.out.println("member.teamClass = " + m.getTeam().getClass());
            System.out.println("member.team = " + m.getTeam().getName());
        });
    }

    @Test
    public void findMemberFetch() {
        // given
        // member1 -> teamA
        // member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // fetch join
        List<Member> members = memberRepository.findMemberFetchJoin();
        members.forEach(m -> {
            System.out.println("member = " + m.getUsername());
            // class study.datajpa.entity.Team
            System.out.println("member.teamClass = " + m.getTeam().getClass());
            System.out.println("member.team = " + m.getTeam().getName());
        });
    }

    @Test
    public void findMemberEntityGraph() {
        // given
        // member1 -> teamA
        // member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // entity graph (내부적으로 fetch join을 한다)
        List<Member> members = memberRepository.findAll();
        members.forEach(m -> {
            System.out.println("member = " + m.getUsername());
            // class study.datajpa.entity.Team
            System.out.println("member.teamClass = " + m.getTeam().getClass());
            System.out.println("member.team = " + m.getTeam().getName());
        });

        List<Member> members2 = memberRepository.findEntityGraphByUsername("member1");
        members2.forEach(m -> {
            System.out.println("member = " + m.getUsername());
            // class study.datajpa.entity.Team
            System.out.println("member.teamClass = " + m.getTeam().getClass());
            System.out.println("member.team = " + m.getTeam().getName());
        });
    }

    @Test
    void queryHint() {
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        em.flush();     // 영속성 컨텍스트 db반영
        em.clear();     // 영속성 컨텍스트 1차 캐시 삭제

        Member findMember = memberRepository.findById(member.getId()).get();
        findMember.setUsername("member2");

        // 변경 감지 -> db에 update쿼리 실행 (변경 감지를 한다는 건 원본을 안다는 것 -> 정보를 두 개 가지고 있는 것. 원본, 변경)
        em.flush();
    }

    @Test
    void queryHint2() {
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        em.flush();     // 영속성 컨텍스트 db반영
        em.clear();     // 영속성 컨텍스트 1차 캐시 삭제

        // 업데이를 db에 굳이 반영하지 않을 거고 값만 바꿀건데 정보를 두 개 관리하는 것은 비효율적 -> JPA 힌트 사용
        // 그렇게 큰 성능 최적화가 되진 않음. 캐싱 등의 다른 방법을 쓰는 게 더 효율적이긴 함
        Member findMember = memberRepository.findReadOnlyByUsername(member.getUsername());
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    void lock() {
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();

        // select문 뒤에  for update 붙음
        List<Member> result = memberRepository.findLockByUsername(member.getUsername());
    }

    @Test
    void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    void projections() {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1");

        for (UsernameOnly usernameOnly : result) {
            System.out.println("usernameOnly = " + usernameOnly);
        }

        List<UsernameOnlyDto> result2 = memberRepository.findProjectionsByUsername("m1", UsernameOnlyDto.class);

        for (UsernameOnlyDto usernameOnly : result2) {
            System.out.println("usernameOnly = " + usernameOnly);
        }

        // 중첩구조는 최적화가 안됨
        List<NestedClosedProjections> result3 = memberRepository.findProjectionsByUsername("m1", NestedClosedProjections.class);

        for (NestedClosedProjections usernameOnly : result3) {
            System.out.println("usernameOnly = " + usernameOnly);
        }
    }
}