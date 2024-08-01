package example.tdd.example1.repository;

import example.tdd.example1.repository.entity.Membership;
import example.tdd.example1.enums.MembershipType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

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

    @DisplayName("내가 가진 멤버십 조회, 멤버십이 X")
    @Test
    void noMembership() {
        // given

        // when
        List<Membership> memberships = memberShipRepository.findAllByUserId("userId");

        // then
        assertThat(memberships.size()).isEqualTo(0);
    }

    @DisplayName("내가 가진 멤버십 조회, 멤버십이 여러개")
    @Test
    void twoMemberships() {
        // given
        Membership membership1 = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.NAVER)
                .point(10000)
                .build();

        Membership membership2 = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.KAKAO)
                .point(10000)
                .build();

        // when
        memberShipRepository.save(membership1);
        memberShipRepository.save(membership2);
        List<Membership> memberships = memberShipRepository.findAllByUserId("userId");
        // then
        assertThat(memberships.size()).isEqualTo(2);
    }
}
