package jpabook.jpashop.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        // N + 1 문제
        List<OrderQueryDto> orders = findOrders();  // Query 1번 -> 결과 N개
        //루프를 돌면서 컬렉션 추가(추가 쿼리 실행)
        orders.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());  // Query N번
            o.setOrderItems(orderItems);
        });

        orders.forEach(order -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(order.getOrderId());
            order.setOrderItems(orderItems);
        });

        return orders;
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate,o.status, d.address)" +
                                " from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto" +
                                "(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> orders = findOrders();  // order에 member, delivery join

        List<Long> orderIds = toOrderIds(orders);

        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(orderIds);

        orders.forEach(order -> order.setOrderItems(orderItemMap.get(order.getOrderId())));

        return orders;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class
                )
                .setParameter("orderIds", orderIds)
                .getResultList();
        // select
        //        oi.order_id,
        //        i.name,
        //        oi.order_price,
        //        oi.count
        //    from
        //        order_item oi
        //    join
        //        item i
        //            on i.item_id=oi.item_id
        //    where
        //        oi.order_id in (?,?)

        return orderItems.stream()
                .collect(Collectors.groupingBy(orderItem -> orderItem.getOrderId()));
    }

    private static List<Long> toOrderIds(List<OrderQueryDto> orders) {
        return orders.stream()
                .map(order -> order.getOrderId())
                .collect(Collectors.toList());
    }

    public List<OrderFlatDto> findAllByDto_flat() {
        // 장점 : 쿼리가 한 번만 나간다 / 단점 : 페이징을 못한다, 중복 데이터가 포함된다.
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderFlatDto(" +
                        "o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" +
                        " join o.orderItems oi" +
                        " join oi.item i", OrderFlatDto.class
        ).getResultList();
        //select
        //    o1_0.order_id,
        //        m1_0.name,
        //        o1_0.order_date,
        //        o1_0.status,
        //        d1_0.city,
        //        d1_0.street,
        //        d1_0.zipcode,
        //        i1_0.name,
        //        o2_0.order_price,
        //        o2_0.count
        //    from
        //        orders o1_0
        //    join
        //        member m1_0
        //            on m1_0.member_id=o1_0.member_id
        //    join
        //        delivery d1_0
        //            on d1_0.delivery_id=o1_0.delivery_id
        //    join
        //        order_item o2_0
        //            on o1_0.order_id=o2_0.order_id
        //    join
        //        item i1_0
        //            on i1_0.item_id=o2_0.item_id
    }
}
