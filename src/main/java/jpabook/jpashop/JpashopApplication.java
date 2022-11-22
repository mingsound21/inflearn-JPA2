package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

	// 간단한 주문 조회 V1: 엔티티를 직접 노출) 프록시 객체 문제 해결을 위한 코드
	@Bean
	Hibernate5Module hibernate5Module(){
		Hibernate5Module hibernate5Module = new Hibernate5Module(); // 기본은 LAZY는 무시(null로)
		// hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true); // 설정하면 LAZY도 그냥 강제로 가져오게 할 수 있긴함
		return  hibernate5Module;
	}
	// 강사님 says) 그렇지만!!! 외부로 엔티티 그대로 노출하면 안됨!!
}

