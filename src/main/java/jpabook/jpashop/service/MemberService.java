package jpabook.jpashop.service;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 읽기전용이 많으니까 이걸 기본으로 깔고
@RequiredArgsConstructor // 생성자 주입 사용
public class MemberService {
    private final MemberRepository memberRepository; // 변경할 이유 없으니 final

    /**
     * 회원 가입
     * */
    @Transactional // 읽기전용 아닌 것에는 다시 @Transactional 작성
    public Long join(Member member){
        validateDuplicateMember(member); //중복 회원 검증
        memberRepository.save(member);
        return member.getId(); // 영속성 컨텍스트에 있는 엔티티는 pk값이 항상 설정되어있음
    }

    // 중복 회원 검증
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }
    // 멀티스레드일 경우를 대비해서 DB에 name을 unique 제약조건 거는 것을 추천
    // => 2개의 서로 다른 스레드에서 동시에 같은 이름의 회원을 join하는 경우, 동시에 중복 회원 검증 함수 호출해서 둘다 통과되는 오류 발생 가능성 있음.


    //회원 전체 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    //회원 1명 조회
    public Member findMember(Long memberId){
        return memberRepository.findOne(memberId);
    }
}
