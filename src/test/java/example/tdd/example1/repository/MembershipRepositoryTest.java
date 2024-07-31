package example.tdd.example1.repository;

import example.tdd.example1.repository.entity.Membership;
import example.tdd.example1.enums.MembershipType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class MembershipRepositoryTest {

    @Autowired
    private MembershipRepository memberShipRepository;


    @DisplayName("멤버십 등록")
    @Test
    void addMembership() {
        // given
        final Membership membership = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.NAVER)
                .point(10000)
                .build();
        // when
        final Membership result = memberShipRepository.save(membership);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getMembershipType()).isEqualTo(result.getMembershipType());
        assertThat(result.getPoint()).isEqualTo(result.getPoint());
        assertThat(result.getUserId()).isEqualTo(result.getUserId());
    }

    @DisplayName("멤버십이 이미 존재하는지 확인")
    @Test
    void alreadyExist() {
        // given
        final Membership membership = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.NAVER)
                .point(10000)
                .build();

        // when
        memberShipRepository.save(membership);
        final Membership findResult = memberShipRepository.findByUserIdAndMembershipType("userId", MembershipType.NAVER);

        // then
        assertThat(findResult).isNotNull();
        assertThat(findResult.getId()).isNotNull();
        assertThat(findResult.getMembershipType()).isEqualTo(membership.getMembershipType());
        assertThat(findResult.getPoint()).isEqualTo(membership.getPoint());
        assertThat(findResult.getUserId()).isEqualTo(membership.getUserId());
    }



}
