package study.datajpa.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import java.util.List;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();

        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        members.forEach(m -> {
            System.out.println("member = " + m);
            System.out.println("-> member.team = " + m.getTeam());
        });
    }

    @Test
    void JpaEventBaseEntity() {
        // given
        Member member = new Member("member1");
        memberRepository.save(member);
        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();
        findMember.setUsername("member2");
        em.flush();
        em.clear();

        // when
        Member updatedMember = memberRepository.findById(member.getId()).get();

        // then
        System.out.println("findMember.createdDate = " + findMember.getCreatedDate());
        System.out.println("updatedMember.updatedDate = " + updatedMember.getLastModifiedDate());
        System.out.println("findMember.getCreatedBy = " + updatedMember.getCreatedBy());
        System.out.println("updatedMember.getLastModifiedBy = " + updatedMember.getLastModifiedBy());
    }
}