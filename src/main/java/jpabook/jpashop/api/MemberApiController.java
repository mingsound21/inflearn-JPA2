package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController // @Controller + @ResponseBody(데이터를 JSON형식으로 보낸다)
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    /**
     * 회원 등록
     */
    // V1, V2 차이 : DTO
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){ // @RequestBody : JSON으로 온 Body를 member으로 바꿔줌
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){ // @RequestBody : JSON으로 온 Body를 member으로 바꿔줌
        Member member = new Member();
        member.setName(request.getName());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }
    // V2는 Member Entity의 스펙 변경해도, API 스펙 영향을 받지 않음


    /**
     * 회원 수정
     */
    @PutMapping("/api/v1/members/{id}")
    public UpdateMemberResponse updateMemberResponseV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request){

        memberService.update(id, request.getName());
        Member findMember = memberService.findMember(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    /**
     * DTO
     */

    /**
     * 등록
     */
    @Data
    static class CreateMemberRequest{
        @NotEmpty // Validation DTO에서
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class CreateMemberResponse{
        private Long id;
    }

    /**
     * 수정
     */
    @Data
    static class UpdateMemberRequest{
        @NotEmpty
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

}