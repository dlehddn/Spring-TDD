package example.tdd.example1.service;

import example.tdd.example1.enums.MembershipType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
public class MembershipResponse {

    private final Long id;
    private final MembershipType membershipType;
}
