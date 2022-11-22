package jpabook.jpashop.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

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
     * 회원 조회
     */
    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){
        return memberService.findMembers();
    }
    // BAD: 엔티티 자체를 반환하면 엔티티에 있는 모든 정보가 다 노출됨

    @GetMapping("/api/v2/members")
    public Result membersV2(){
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());
        return new Result(collect);
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

    /**
     * 조회
     */
    @Data
    @AllArgsConstructor
    static class Result<T>{
        private T data;
    }
    // List 컬렉션을 바로 반환하면 배열형태로 나감
    // 이런식으로 한번 감싸서 반환해야함 => 유연성 UP

    @Data
    @AllArgsConstructor
    static class MemberDto{
        private String name;
    }

}
