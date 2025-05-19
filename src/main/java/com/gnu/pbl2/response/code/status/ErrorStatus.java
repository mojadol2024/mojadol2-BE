package com.gnu.pbl2.response.code.status;

import com.gnu.pbl2.response.code.BaseErrorCode;
import com.gnu.pbl2.response.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 일반적인 응답
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // Token 관련 응답
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN4001", "액세스 토큰이 만료되었습니다."),
    ACCESS_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN4002", "잘못된 토큰입니다."),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4003", "지원되지 않는 JWT 토큰입니다."),
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4004", "JWT 토큰이 잘못되었습니다."),
    REFRESH_TOKEN_NOT_VALID(HttpStatus.UNAUTHORIZED, "TOKEN4005", "Refresh Token 정보가 유효하지 않습니다."),
    REFRESH_TOKEN_NOT_MATCH(HttpStatus.UNAUTHORIZED, "TOKEN4006", "Refresh Token 정보가 일치하지 않습니다."),
    TOKEN_NO_AUTHORITY(HttpStatus.UNAUTHORIZED, "TOKEN4007", "권한 정보가 없는 토큰입니다."),
    NO_AUTHENTICATION_INFORMATION(HttpStatus.UNAUTHORIZED, "TOKEN4008", "인증 정보가 없는 토큰입니다."),
    JWT_FILTER_ERROR(HttpStatus.UNAUTHORIZED, "TOKEN4300", "JWT FILTER 에러"),

    // User 관련 응답
    USER_ID_IN_USE(HttpStatus.CONFLICT, "USER4000", "사용 중인 유저아이디입니다."),
    USER_NICKNAME_IN_USE(HttpStatus.CONFLICT, "USER4001", "사용 중인 닉네임입니다."),
    USER_NOT_AUTHORITY(HttpStatus.FORBIDDEN, "USER4003", "권한이 없습니다."),
    USER_EMAIL_IN_USE(HttpStatus.CONFLICT, "USER4004", "사용 중인 이메일입니다."),
    USER_PHONE_NUMBER_IN_USE(HttpStatus.CONFLICT, "USER4005", "사용 중인 전화번호입니다."),

    USER_SQL_UNIQUE(HttpStatus.CONFLICT, "USER409", "중복된 데이터가 있습니다."),
    USER_BAD_REQUEST(HttpStatus.BAD_REQUEST, "USER400", "데이터가 잘못 되었습니다."),

    USER_RESIGN_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "USER5000", "회원탈퇴에 실패하였습니다."),
    USER_DELETE_SCHEDULE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "USER5001","회원 삭제 스케줄러 실행 중 오류 발생"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4001", "사용자가 존재하지 않습니다."),
    USER_BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH4002", "아이디 또는 비밀번호가 일치하지 않습니다."),



    // Mail 관련 응답
    MAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MAIL5000", "메일 전송에 실패하였습니다."),
    MAIL_INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "MAIL4000", "유효하지 않은 이메일 주소입니다."),
    MAIL_SERVER_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "MAIL5030", "메일 서버가 응답하지 않습니다."),
    MAIL_TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "MAIL4040", "메일 템플릿을 찾을 수 없습니다."),
    MAIL_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4040", "해당 이메일을 가진 사용자를 찾을 수 없습니다."),
    MAIL_PROCESS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MAIL5001", "메일 처리 중 오류가 발생하였습니다."),

    // CoverLetter 관련 응답
    COVER_LETTER_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COVER_LETTER5000", "COVER LETTER SERVER ERROR."),
    COVER_LETTER_NOT_FOUND(HttpStatus.NOT_FOUND, "COVER_LETTER4004", "자소서 데이터가 없습니다."),
    COVER_LETTER_BAD_REQUEST(HttpStatus.BAD_REQUEST, "COVER_LETTER4000", "자소서 데이터가 없습니다."),
    COVER_LETTER_JSON_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COVER_LETTER5002", "데이터 파싱 중 에러가 발생했습니다. 변경태한테 물어보세요."),
    SPELLCHECKER_PASSPORTKEY_NOT_FOUND(HttpStatus.NOT_FOUND, "COVER_LETTER4040", "맞춤법 검사 키가 없습니다. 변경태한테 물어보세요."),
    SPELLCHECKER_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SPELLCHECKER5001", "맞춤법 검사 중 서버에 에러가 발생하였습니다."),

    INTERVIEW_SFTP_CONNECT_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "SFTP_CONNECT_ERROR 5003", "sftp 연결 실패했습니다."),
    INTERVIEW_SFTP_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SFTP_CONNECT_ERROR 5004", "sftp 삭제 실패했습니다."),
    INTERVIEW_DELETE_ERROR(HttpStatus.NOT_FOUND, "SFTP_CONNECT_ERROR 5002", "인터뷰 영상 삭제 실패했습니다."),
    INTERVIEW_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SFTP_CONNECT_ERROR 5001", "인터뷰 영상 저장 실패했습니다."),
    INTERVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "SFTP_CONNECT_ERROR 4004", "인터뷰 영상을 못 찾았습니다."),
    INTERVIEW_SCHEDULE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SFTP_CONNECT_ERROR 4004", "영상삭제 스케줄링에서 에러 발생"),
    //INTERVIEW_DIRECTORY_NOTFOUND_ERROR(HttpStatus.NOT_FOUND, "SFTP_NOTFOUND_ERROR 5004", "sftp 디렉토리를 못 찾았습니다.")


    // Payment 관련 응답
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_ERROR 4004", "결제내역을 못 찾았습니다."),
    PAYMENT_FORBIDDEN(HttpStatus.FORBIDDEN, "PAYMENT_FORBIDDEN 4005", "본인 정보만 열람 할 수 있습니다."),


    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "QUESTION_NOT_FOUND 4004", "질문 내용이 없습니다."),
    QUESTION_BAD_REQUEST(HttpStatus.BAD_REQUEST, "QUESTION_NOT_FOUND 4000", "질문 생성 에러"),
    QUESTION_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "QUESTION_INTERNAL_SERVER_ERROR 5000", "질문관련 서버 에러")

    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }

}
