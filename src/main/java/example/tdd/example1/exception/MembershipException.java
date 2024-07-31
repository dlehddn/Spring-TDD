package example.tdd.example1.exception;

import example.tdd.example1.enums.MembershipErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MembershipException extends RuntimeException {


    private final MembershipErrorResult errorResult;

}
