package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model){
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result){

        if(result.hasErrors()){
            return "members/createMemberForm"; // 에러있더라도 form 데이터 가져가서, 입력한 값들은 유지됨(도시, 거리, 우편번호 입력시)
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model){ // Model이라는 객체 통해서 화면에 객체 전달
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
    // Q. 회원 가입일 땐 폼 객체를 사용해서 정보를 받았는데, 회원 목록 조회는 그냥 Member 엔티티 사용?
    // A. 단순해서 Member 엔티티 자체를 사용했을 뿐, 실무에서는 DTO로 변환해서 화면에 꼭 필요한 것만 가진 객체를 만드는 것이 좋다!
    // 단, api를 만들때에는 이유불문 엔티티 외부로 넘겨서는 안됨
    // => 엔티티 변경 시, api 스펙(response body)가 변함 + 개인정보 노출 가능성
}
