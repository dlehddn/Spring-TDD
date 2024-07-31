package example.tdd.repository;

import example.tdd.repository.entity.Membership;
import example.tdd.repository.enums.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    Membership findByUserIdAndMembershipType(final String userId, final MembershipType membershipType);
}
