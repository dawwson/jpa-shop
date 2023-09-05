package jpabook.jpashop.service.dto;

import jpabook.jpashop.controller.BookForm;
import lombok.Getter;

@Getter
public class UpdateBookDto {
    String name;
    int price;
    int stockQuantity;
    String author;
    String isbn;

    /* 생성 메서드 */

    /**
     * ItemService에서 사용하는 dto 생성
     * @param form 뷰에서 받아온 수정할 모델 데이터
     * @return UpdateBookDto 인스턴스
     */
    public static UpdateBookDto create(BookForm form
    ) {
        UpdateBookDto updateBookDto = new UpdateBookDto();
        updateBookDto.name = form.getName();
        updateBookDto.price = form.getPrice();
        updateBookDto.stockQuantity = form.getStockQuantity();
        updateBookDto.author = form.getAuthor();
        updateBookDto.isbn = form.getIsbn();
        return updateBookDto;
    }
}
