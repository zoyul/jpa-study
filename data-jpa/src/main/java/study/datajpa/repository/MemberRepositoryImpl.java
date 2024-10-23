package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import java.util.List;

// 클래스 이름 : MemberRepository + Impl 무조건 이름을 맞춰 주어야 한다
// MemberRepositoryImpl 를 MemberRepository 가 상속받아야 한다
// MemberRepository 에서 findMemberCustom 메서드를 호출했을 때 알아서 구현체 MemberRepositoryImpl를 실행해줌
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
