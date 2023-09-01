package jpabook.jpashop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("새 멤버 생성")
    @Transactional   // 테스트 종료 후 데이터 롤백
    @Rollback(false) // 테스트 종료 후 롤백 안 함
    void testMember() {
        // given
        Member member = new Member();
        member.setUsername("memberA");

        // when
        Long savedId = memberRepository.save(member);
        Member found = memberRepository.find(savedId);

        // then
        assertThat(found.getId()).isEqualTo(member.getId());
        assertThat(found.getUsername()).isEqualTo(member.getUsername());
        assertThat(found).isEqualTo(member);
    }
}