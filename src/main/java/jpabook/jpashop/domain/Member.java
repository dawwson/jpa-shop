package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
    // 컬렉션은 필드에서 초기화 => 엔티티를 영속화할 때 하이버네이트 내장 컬렉션으로 변경됨
    // setter나 생성자로 변경하게 되면 하이버네이트 내부 메커니즘에 문제 발생 가능성 있음.
}
