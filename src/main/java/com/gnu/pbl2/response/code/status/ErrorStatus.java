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

    // User 관련 응답
    USER_ID_IN_USE(HttpStatus.CONFLICT, "USER4000", "사용 중인 유저아이디입니다."),
    USER_NICKNAME_IN_USE(HttpStatus.CONFLICT, "USER4001", "사용 중인 닉네임입니다."),
    MASTER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4002", "해당 유저가 없습니다."),
    MASTER_NOT_AUTHORITY(HttpStatus.FORBIDDEN, "USER4003", "권한이 없습니다."),
    USER_EMAIL_IN_USE(HttpStatus.CONFLICT, "USER4004", "사용 중인 이메일입니다."),
    USER_PHONENUMBER_IN_USE(HttpStatus.CONFLICT, "USER4005", "사용 중인 전화번호입니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH4001", "아이디 또는 비밀번호가 일치하지 않습니다."),


    // LostItem 관련 에러
    LOST_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "LOSTITEM404", "해당 분실물 게시글을 찾을 수 없습니다."),
    LOST_ITEM_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "LOSTITEM400", "분실물 요청 데이터가 유효하지 않습니다."),
    LOST_ITEM_ALREADY_EXISTS(HttpStatus.CONFLICT, "LOSTITEM409", "해당 분실물 게시글이 이미 존재합니다."),
    LOST_ITEM_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "LOSTITEM5001", "분실물 게시글 삭제에 실패하였습니다."),
    LOST_ITEM_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "LOSTITEM5002", "분실물 게시글 수정에 실패하였습니다."),

    // Menu 관련 에러
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "MENU404", "메뉴를 찾을 수 없습니다."),
    MENU_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "MENU400", "메뉴 요청 데이터가 유효하지 않습니다."),

    // Notification 관련 에러
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION404", "안내 게시글을 찾을 수 없습니다."),
    NOTIFICATION_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "NOTIFICATION400", "안내 게시글 요청 데이터가 유효하지 않습니다."),

    // Rent 관련 에러
    RENT_NOT_FOUND(HttpStatus.NOT_FOUND, "RENT404", "렌트 물품을 찾을 수 없습니다."),
    RENT_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "RENT400", "렌트 게시글 요청 데이터가 유효하지 않습니다."),
    RENT_ITEM_OUT_OF_STOCK(HttpStatus.CONFLICT, "RENT409", "렌트물건의 현재 수량이 부족합니다."),

    // ChatUrl 관련 에러
    URL_NOT_FOUND(HttpStatus.NOT_FOUND, "URL404", "URL을 찾을 수 없습니다."),
    URL_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "URL400", "URL 요청 데이터가 유효하지 않습니다."),

    // Coalition 관련 에러
    COALITION_NOT_FOUND(HttpStatus.NOT_FOUND, "COALITION404", "제휴 업체를 찾을 수 없습니다."),
    COALITION_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COALITION400", "게시글 요청 데이터가 유효하지 않습니다."),

    //게시글 관련 에러
    STUDENT_COUNCIL_BOARD_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "COUNCIL5000", "총학생회 작성 게시물을 찾지 못했습니다."),
    STUDENT_COUNCIL_BOARD_POST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "COUNCIL5001", "총학생회 게시물 작성에 실패하였습니다.")
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
