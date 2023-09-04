package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
도메인 모델 패턴으로 작성 => 도메인 주도 설계
- 엔티티가 비즈니스 로직을 가지고, 서비스는 엔티티를 호출하는 정도의 얇은 비즈니스 로직을 가짐
- 트랜잭션 스크립트 패턴(서비스에서 핵심 비즈니스 로직 구현)과 도메인 모델 패턴 중 정답은 없다.
- 둘이 혼용되어 있을 수도 있고, 상황에 맞게 적절히 선택하는 게 좋다.
 */
@Service
@Transactional(readOnly = true)  // TODO: 트랜잭셔널은 왜 서비스에만 있는건지??
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /*
     * 주문 생성
     */
    @Transactional
    public Long createOrder(Long memberId, Long itemId, int count) {
        // 엔티티 조회
        Member member = memberRepository.findById(memberId);
        Item item = itemRepository.findById(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문상품 생성
        OrderItem orderItem = OrderItem.create(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.create(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order);  // cascade 옵션으로 order만 저장해도 orderItem, delivery가 저장된다.

        return order.getId();
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        // 주문 엔티티 조회
        Order order = orderRepository.findById(orderId);

        // 주문 취소
        order.cancel();  // 객체의 프로퍼티만 변경해주면 JPA가 dirty checking 하여 업데이트 해준다.
    }

    /**
     * 주문 검색
     */
    //public List<Order> searchOrders(OrderSearch orderSearch) {
    //    return orderRepository.findOrders(orderSearch);
    //}
}
