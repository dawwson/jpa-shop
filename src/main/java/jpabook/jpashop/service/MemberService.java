package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;  // 스프링이 제공하는 트랜잭션 사용 권장

import java.util.List;

@Service
@Transactional(readOnly = true)  // 읽기전용 메서드에 사용
@RequiredArgsConstructor  // final 붙은 의존 관계를 생성자 주입해줌
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     */
    @Transactional  // 쓰기를 하는 메서드에는 readOnly = false로 적용
    public Long join(Member member) {
        // 중복 회원 검증
        validateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    // 중복 회원 검증
    private void validateMember(Member member) {
        List<Member> members = memberRepository.findByName(member.getName());

        if (!members.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 회원 전체 조회
     * @return 모든 회원 리스트
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }

    @Transactional
    public void updateMember(Long id, String name) {
        Member member = memberRepository.findById(id);
        member.setName(name);  // 변경 감지
    }
}
