package example.tdd.example1.service;

import example.tdd.example1.enums.MembershipType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class MembershipDetailResponse {
    private final Long id;
    private final MembershipType membershipType;
}
