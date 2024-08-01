package example.tdd.example1.service;

import example.tdd.example1.enums.MembershipErrorResult;
import example.tdd.example1.enums.MembershipType;
import example.tdd.example1.exception.MembershipException;
import example.tdd.example1.repository.MembershipRepository;
import example.tdd.example1.repository.entity.Membership;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;

    public MembershipResponse addMembership(final String userId, final MembershipType membershipType, final Integer point) {
        Membership result = membershipRepository.findByUserIdAndMembershipType(userId, membershipType);
        if (result != null) {
            throw new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);
        }
        Membership membership = membershipRepository.save(Membership.builder()
                .userId(userId)
                .membershipType(membershipType)
                .point(point)
                .build());

        return MembershipResponse.builder()
                .id(membership.getId())
                .membershipType(membership.getMembershipType())
                .build();
    }

    public List<MembershipDetailResponse> getMyMemberships(String userId) {
        List<Membership> memberships = membershipRepository.findAllByUserId(userId);
        return memberships.stream()
                .map(m -> MembershipDetailResponse.builder()
                        .id(m.getId())
                        .membershipType(m.getMembershipType())
                        .build())
                .collect(Collectors.toList());
    }
}
