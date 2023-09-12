package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController  // @Controller + @ResponseBody
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(
            @RequestBody @Valid Member member
    ) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(
            @RequestBody @Valid CreateMemberRequest request
    ) {
        Member member = new Member();
        member.setName(request.getName());

        Long newMemberId = memberService.join(member);
        return new CreateMemberResponse(newMemberId);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request
    ) {
        // 커맨드-쿼리 분리 : 값을 변경했지만 반환을 하진 않는다.
        memberService.updateMember(id, request.getName());
        Member updatedMember = memberService.findMember(id);
        return new UpdateMemberResponse(updatedMember.getId(), updatedMember.getName());
    }

    // 엔티티를 파라미터로 받거나 외부로 그대로 노출하는 일 방지 => 엔티티와 API 스펙 분리 필수!!
    @Data
    private static class CreateMemberRequest {
        @NotEmpty  // 프레젠테이션 계층 검증 로직을 엔티티가 아닌 DTO에 작성
        private String name;
    }

    @Data
    private static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    private static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    private static class UpdateMemberResponse {
        private Long id;
        private String name;
    }
}
