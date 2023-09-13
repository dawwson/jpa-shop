package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findById(Long orderId) {
        return em.find(Order.class, orderId);
    }

    // TODO: querydsl로 리팩토링
    public List<Order> findAll(OrderSearch orderSearch) {

        //language=JPQL
        String jpql = "select o From Order o join o.member m";

        boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query =
                em.createQuery(jpql, Order.class).setMaxResults(1000); //최대 1000건

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }

        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }

    // fetch join(JPQL 문법) : Lazy 로딩 옵션 무시하고 join 해서 한 번의 쿼리로 값을 다 채워서 가져온다.
    // 여러 api에서 재사용 가능
    public List<Order> findAllWithMemberAndDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
                ).getResultList();
    }

    public List<Order> findAllWithMemberAndDelivery(int offset, int limit) {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Order> findAllWithItem() {
        // db에 distinct를 포함해서 쿼리 => order의 id 기준으로 중복이 있으면 중복 제거
        // (스프링부트 3부터는 hibernate 6을 사용하는데 fetch join 시 자동으로 중복 제거를 해줌)
        // 단점 : 페이징을 못함(offset, limit 쿼리가 안 날라감)
        // (하이버네이트는 모든 데이터를 DB에서 읽어오고, 메모리에서 페이징 해버린다(데이터 많으면 위험함)
        return em.createQuery(
                "select distinct o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item i", Order.class
                )
                .getResultList();
    }
}
