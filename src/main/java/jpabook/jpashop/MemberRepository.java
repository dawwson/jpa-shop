package jpabook.jpashop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {
    @PersistenceContext
    private EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();  // 커맨드랑 쿼리를 분리해라(저장 후 리턴값은 id 정도만)
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
