package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.UpdateItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService; // final 작성 안했더니 주입 못받아서 계속 오류 났음!!

    @GetMapping("/items/new")
    public String createForm(Model model){
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form){
        // 리팩토링: setter 대신 createBook 정적 팩토리 메서드 만드는 게 좋음. + setter 삭제
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/items";
    }

    @GetMapping("/items")
    public String list(Model model){
        System.out.println("im controller");
        List<Item> items = itemService.findItems();

        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model){
         Book item = (Book) itemService.findOne(itemId); // 타입 캐스팅 : 예제 간단히 하기 위해서 책만 넘어온다고 가정

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);

        return "items/updateItemForm";
    }

    @PostMapping("/items/{itemId}/edit")
    public String updateItem(@PathVariable("itemId") Long itemId, @ModelAttribute("form") BookForm form) {// html에서 보내줄 때 form이라는 이름의 객체로 넘겨줌

        // 이 Book 객체는 새로운 객체이긴 하지만, id가 있는 JPA에 한번 들어갔다 나온 애임
        // 준영속 상태의 객체 : DB에 한번 들어갔다나와서 id가 있는 객체
/*        Book book = new Book();
        book.setId(form.getId());
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());
        itemService.saveItem(book);
*/

        // << 컨트롤러에서 어설프게 엔티티를 생성하지 마세요 >>
        // 트랜잭션이 있는 서비스 계층에 id와 변경할 데이터를 명확히 전달

        // 방법1) 파라미터
//        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());

        // 방법2) 넘길 값이 많다! >> DTO
        itemService.updateItem(itemId, new UpdateItemDto(form.getName(), form.getPrice(), form.getStockQuantity()));
        return "redirect:/items";
    }
    // 참고!) 실무에서는 id를조심해야함. id를 조작해서 넘길 수 있음 => 다른 사람 데이터 수정될 수 있음.
    // 어떤 계층에서는 유저가 이 id에 대해서 권한이 있는지 체크해야함.

}
