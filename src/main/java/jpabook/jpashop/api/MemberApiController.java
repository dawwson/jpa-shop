package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController  // @Controller + @ResponseBody
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> getMembersV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result getMembersV2() {
        List<Member> members = memberService.findMembers();
        List<MemberDto> memberDtos = members
                .stream()
                .map(member -> new MemberDto(member.getName()))
                .collect(Collectors.toList());
        return new Result(memberDtos);
    }

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

    @Data
    @AllArgsConstructor
    private static class Result<T> {
        // 'data' 필드로 한 번 감싸서 나가면 좋다 -> 나중에 다른 필드가 추가할 수도 있음(count 같은...)
        private T data;
    }

    @Data
    @AllArgsConstructor
    private static class MemberDto {  // TODO: 이것만 네이밍이 xxxResponse가 아닌 이유는?
        // 필요한 부분만 노출
        private String name;
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
