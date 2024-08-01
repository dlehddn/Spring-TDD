package example.tdd.example1.controller;

import example.tdd.example1.service.MembershipDetailResponse;
import example.tdd.example1.service.MembershipResponse;
import example.tdd.example1.service.MembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static example.tdd.example1.controller.MembershipConstants.*;

@RestController
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping("/api/v1/memberships")
    public ResponseEntity<MembershipResponse> addMembership(
            @RequestHeader(USER_ID_HEADER) final String userId,
            @RequestBody @Valid final MembershipRequest membershipRequest) {

        MembershipResponse membershipResponse = membershipService.addMembership(userId, membershipRequest.getMembershipType(), membershipRequest.getPoint());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(membershipResponse);
    }

    @GetMapping("/api/v1/memberships")
    public ResponseEntity<List<MembershipDetailResponse>> getMembership(
            @RequestHeader(USER_ID_HEADER) final String userId) {
        return ResponseEntity.ok(membershipService.getMyMemberships(userId));
    }
}
