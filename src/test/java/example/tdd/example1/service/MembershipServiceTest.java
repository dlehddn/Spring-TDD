package example.tdd.example1.service;

import example.tdd.example1.enums.MembershipErrorResult;
import example.tdd.example1.exception.MembershipException;
import example.tdd.example1.repository.MembershipRepository;
import example.tdd.example1.repository.entity.Membership;
import example.tdd.example1.enums.MembershipType;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MembershipServiceTest {

    private final String userId = "userId";
    private final MembershipType membershipType = MembershipType.NAVER;
    private final Integer point = 10000;




    @InjectMocks
    private MembershipService memberService;

    @Mock
    private MembershipRepository membershipRepository;

    @DisplayName("멤버십등록실패_이미존재함")
    @Test
    void alreadyExist() {
        // given
        doReturn(Membership.builder().build()).when(membershipRepository).findByUserIdAndMembershipType(userId, membershipType);

        // when
        final MembershipException result = assertThrows(MembershipException.class, () -> memberService.addMembership(userId, membershipType, point));

        // then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);
    }

    @DisplayName("멤버십 등록 성공")
    @Test
    void successRegister() {
        // given
        doReturn(null).when(membershipRepository).findByUserIdAndMembershipType(userId, membershipType);
        doReturn(membership()).when(membershipRepository).save(any(Membership.class));

        // when
        MembershipResponse result = memberService.addMembership(userId, membershipType, point);

        // then
        verify(membershipRepository, times(1)).findByUserIdAndMembershipType(userId, membershipType);
        verify(membershipRepository, times(1)).save(any(Membership.class));
        assertThat(result.getId()).isNotNull();
        assertThat(result.getMembershipType()).isEqualTo(MembershipType.NAVER);
    }

    private Membership membership() {
        return Membership.builder()
                .id(-1L)
                .userId(userId)
                .membershipType(membershipType)
                .point(point)
                .build();
    }

    @DisplayName("내가 가진 멤버십 전체 조회")
    @Test
    void getMyMemberships() {
        // given
        doReturn(Arrays.asList(
                Membership.builder().build(),
                Membership.builder().build(),
                Membership.builder().build()
        )).when(membershipRepository).findAllByUserId("userId");

        // when
        List<MembershipDetailResponse> results = memberService.getMyMemberships("userId");

        // then
        assertThat(results.size()).isEqualTo(3);
    }


}
