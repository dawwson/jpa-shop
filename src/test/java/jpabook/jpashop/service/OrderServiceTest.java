package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    void 상품_주문() {
        // given
        Member member = createMember();
        Item book = createBook("폭풍의 언덕", 10000, 10);
        int orderCount = 2;

        // when
        Long orderId = orderService.createOrder(member.getId(), book.getId(), orderCount);

        // then
        Order newOrder = orderRepository.findById(orderId);

        // 상품 주문시 상태는 ORDER
        assertThat(newOrder.getStatus()).isEqualTo(OrderStatus.ORDER);
        // 주문한 상품 종류 수가 정확해야 한다.
        assertThat(newOrder.getOrderItems().size()).isEqualTo(1);
        // 주문 가격은 가격 * 수량이다.
        assertThat(newOrder.getTotalPrice()).isEqualTo(10000 * 2);
        // 주문 수량만큼 재고가 줄어야 한다.
        assertThat(book.getStockQuantity()).isEqualTo(8);
    }

    @Test()
    void 상품주문_재고수량초과() {
        // given
        Member member = createMember();
        Item book = createBook("폭풍의 언덕", 10000, 10);
        int orderCount = 11;  // 재고보다 많은 수량

        // when-then
        assertThrows(
                NotEnoughStockException.class,
                () -> orderService.createOrder(member.getId(), book.getId(), orderCount)
        );
    }

    @Test
    void 주문_취소() {
        // given
        Member member = createMember();
        Item book = createBook("인간실격", 7000, 10);
        int orderCount = 2;

        Long orderId = orderService.createOrder(member.getId(), book.getId(), orderCount);

        // when
        orderService.cancelOrder(orderId);

        // then
        Order canceledOrder = orderRepository.findById(orderId);

        // 주문 취소시 상태는 CANCEL
        assertThat(canceledOrder.getStatus()).isEqualTo(OrderStatus.CANCEL);
        // 주문 취소시 재고는 그만큼 복구되어야 한다.
        assertThat(book.getStockQuantity()).isEqualTo(10);
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울특별시", "종로1", "123-123"));
        em.persist(member);
        return member;
    }

    private Item createBook(String name, int price, int stockQuantity) {
        Item book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }
}