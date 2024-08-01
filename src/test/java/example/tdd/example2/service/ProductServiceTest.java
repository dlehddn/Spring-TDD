package example.tdd.example2.service;


import example.tdd.example2.repository.Product;
import example.tdd.example2.repository.ProductRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private final String productName = "productName";
    private final int quantity = 100;
    private final int price = 1000;

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @DisplayName("이미 등록된 상품, 등록 불가")
    @Test
    void failRegister_AlreadyRegistered() {
        // given
        doThrow(DataIntegrityViolationException.class).when(productRepository).save(any(Product.class));

        // when
        ProductException result = assertThrows(ProductException.class, () -> productService.addProduct(productName, quantity, price));

        // then
        assertThat(result.getErrorCode()).isEqualTo(ProductErrorCode.ALREADY_REGISTERED);
    }

    @DisplayName("상품 등록 성공")
    @Test
    void successRegister() {
        // given
        doReturn(returnProduct()).when(productRepository).save(any(Product.class));

        // when
        Product result = productService.addProduct(productName, quantity, price);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getPrice()).isEqualTo(price);
        assertThat(result.getQuantity()).isEqualTo(quantity);
        assertThat(result.getName()).isEqualTo(productName);
    }

    private Product returnProduct() {
        return Product.builder()
                .id(-1L)
                .name(productName)
                .quantity(quantity)
                .price(price)
                .build();
    }


    public interface ErrorCode {
        HttpStatus getHttpStatus();
        String getMessage();
    }

    @Getter
    @RequiredArgsConstructor
    public enum ProductErrorCode implements ErrorCode {
        ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "이미 등록된 상품입니다.");

        private final HttpStatus httpStatus;
        private final String message;
    }


    @Getter
    public static class BusinessException extends RuntimeException {
        private final ErrorCode errorCode;

        public BusinessException(Throwable cause, ErrorCode errorCode) {
            super(cause);
            this.errorCode = errorCode;
        }
    }

    public static class ProductException extends BusinessException {
        public ProductException(Throwable cause, ErrorCode errorCode) {
            super(cause, errorCode);
        }
    }

    @Service
    @RequiredArgsConstructor
    static class ProductService {

        private final ProductRepository productRepository;

        public Product addProduct(String productName, Integer quantity, Integer price) {
            try {
                return productRepository.save(Product.builder()
                        .name(productName)
                        .quantity(quantity)
                        .price(price)
                        .build());
            } catch (DataIntegrityViolationException e) {
                throw new ProductException(e, ProductErrorCode.ALREADY_REGISTERED);
            }
        }
    }



}
