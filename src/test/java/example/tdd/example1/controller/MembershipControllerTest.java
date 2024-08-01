package example.tdd.example1.controller;

import com.google.gson.Gson;
import example.tdd.example1.enums.MembershipErrorResult;
import example.tdd.example1.enums.MembershipType;
import example.tdd.example1.exception.MembershipException;
import example.tdd.example1.service.MembershipDetailResponse;
import example.tdd.example1.service.MembershipResponse;
import example.tdd.example1.service.MembershipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;

import static example.tdd.example1.controller.MembershipConstants.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class MembershipControllerTest {

    @InjectMocks
    private MembershipController membershipController;

    @Mock
    private MembershipService membershipService;

    private MockMvc mockMvc;
    private Gson gson;


    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(membershipController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        gson = new Gson();
    }

    @DisplayName("mockMvc는 null이 아님")
    @Test
    void mockMvcIsNotNull() {
        // given

        // when

        // then
        assertThat(membershipController).isNotNull();
        assertThat(mockMvc).isNotNull();
    }

    @DisplayName("멤버십 등록 실패, 사용자 식별값이 헤더에 없음")
    @Test
    void failCauseNoHeader() throws Exception {
        // given
        final String url = "/api/v1/memberships";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(membershipRequest(10000, MembershipType.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @DisplayName("멤버십 등록 실패, 포인트가 음수")
    @Test
    void failCauseMinusPoint() throws Exception {
        // given
        final String url = "/api/v1/memberships";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER, "userId")
                        .content(gson.toJson(membershipRequest(-1, MembershipType.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @DisplayName("멤버십 등록 실패, 멤버십종류가 null")
    @Test
    void failCauseTypeIsNull() throws Exception {
        // given
        final String url = "/api/v1/memberships";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER, "userId")
                        .content(gson.toJson(membershipRequest(-1, null)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @DisplayName("파라미터만 다른 비슷한 테스트 묶기, 멤버십 등록 실패")
    @ParameterizedTest
    @MethodSource("invalidMembershipAddParameter")
    void testMethodNameHere(final Integer point, final MembershipType membershipType) throws Exception {
        final String url = "/api/v1/memberships";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER, "userId")
                        .content(gson.toJson(membershipRequest(point, membershipType)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> invalidMembershipAddParameter() {
        return Stream.of(
                Arguments.of(null, MembershipType.NAVER),
                Arguments.of(-1, MembershipType.NAVER),
                Arguments.of(10000, null)
        );
    }

    @DisplayName("멤버십 등록 실패, throw MembershipException 발생")
    @Test
    void failCauseException() throws Exception {
        // given
        final String url = "/api/v1/memberships";
        doThrow(new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER))
                .when(membershipService)
                .addMembership("userId", MembershipType.NAVER, 10000);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER, "userId")
                        .content(gson.toJson(MembershipRequest.builder()
                                .point(10000)
                                .membershipType(MembershipType.NAVER)
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON)

        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @DisplayName("멤버십 등록 성공")
    @Test
    void success() throws Exception {
        // given
        final String url = "/api/v1/memberships";
        final MembershipResponse membershipResponse = MembershipResponse.builder()
                .id(-1L)
                .membershipType(MembershipType.NAVER)
                .build();
        doReturn(membershipResponse).when(membershipService).addMembership("userId", MembershipType.NAVER, 10000);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER, "userId")
                        .content(gson.toJson(MembershipRequest.builder()
                                .point(10000)
                                .membershipType(MembershipType.NAVER)
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isCreated());

        final MembershipResponse response = gson.fromJson(resultActions.andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8), MembershipResponse.class);

        assertThat(response.getMembershipType()).isEqualTo(MembershipType.NAVER);
        assertThat(response.getId()).isEqualTo(-1L);
    }


    private MembershipRequest membershipRequest(Integer point, MembershipType membershipType) {
        return MembershipRequest.builder()
                .point(point)
                .membershipType(membershipType)
                .build();
    }

    @DisplayName("멤버십 목록 조회 실패, 헤더에 식별값이 존재하지 않음")
    @Test
    void noHeader() throws Exception {
        // given
        final String url = "/api/v1/memberships";

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @DisplayName("멤버십 목록 조회 성공")
    @Test
    void successGetMyMemberships() throws Exception {
        final String url = "/api/v1/memberships";
        doReturn(Arrays.asList(
                MembershipDetailResponse.builder().build(),
                MembershipDetailResponse.builder().build(),
                MembershipDetailResponse.builder().build()
        )).when(membershipService).getMyMemberships(any(String.class));

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(url)
                        .header(USER_ID_HEADER, "userId")
        );

        // then
        resultActions.andExpect(status().isOk());
    }
}
