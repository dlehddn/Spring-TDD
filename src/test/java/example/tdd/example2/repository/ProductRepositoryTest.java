package example.tdd.example2.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("상품 등록 실패, 동일한 이름의 상품")
    @Test
    void failRegisterProduct() {
        // given
        Product product = createProduct();
        Product product2 = createProduct();
        // when
        productRepository.save(product);
        // then
        assertThatThrownBy(() -> productRepository.save(product2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("상품 등록 성공")
    @Test
    void successRegisterProduct() {
        // given
        Product product = createProduct();

        // when
        Product result = productRepository.save(product);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("productName");
        assertThat(result.getQuantity()).isEqualTo(0);
        assertThat(result.getPrice()).isEqualTo(10000);
    }

    private static Product createProduct() {
        Product product = Product.builder()
                .name("productName")

                .quantity(0)
                .price(10000)
                .build();
        return product;
    }}
