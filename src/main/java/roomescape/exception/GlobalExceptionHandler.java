package roomescape.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.dto.error.ErrorCode;
import roomescape.dto.error.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidInput(InvalidInputException e) {
        return new ErrorResponse(ErrorCode.INVALID_INPUT.getCode(), e.getMessage());
    }

    @ExceptionHandler(PastReservationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlePastReservation(PastReservationException e) {
        return new ErrorResponse(ErrorCode.PAST_RESERVATION.getCode(), e.getMessage());
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleReservationNotFound(ReservationNotFoundException e) {
        return new ErrorResponse(ErrorCode.RESERVATION_NOT_FOUND.getCode(), e.getMessage());
    }

    @ExceptionHandler(ReservationTimeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleTimeNotFound(ReservationTimeNotFoundException e) {
        return new ErrorResponse(ErrorCode.RESERVATION_TIME_NOT_FOUND.getCode(), e.getMessage());
    }

    @ExceptionHandler(DuplicateReservationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateReservation(DuplicateReservationException e) {
        return new ErrorResponse(ErrorCode.DUPLICATE_RESERVATION.getCode(), e.getMessage());
    }

    @ExceptionHandler(ReservationTimeInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleTimeInUse(ReservationTimeInUseException e) {
        return new ErrorResponse(ErrorCode.RESERVATION_TIME_IN_USE.getCode(), e.getMessage());
    }

    @ExceptionHandler(UnauthorizedReservationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleUnauthorized(UnauthorizedReservationException e) {
        return new ErrorResponse(ErrorCode.UNAUTHORIZED_RESERVATION.getCode(), e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthentication(AuthenticationException e) {
        return new ErrorResponse(e.getErrorCode().getCode(), e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        Throwable rootCause = e.getRootCause();
        if (rootCause instanceof InvalidInputException) {
            return new ErrorResponse(ErrorCode.INVALID_INPUT.getCode(), rootCause.getMessage());
        }
        return new ErrorResponse(ErrorCode.INVALID_REQUEST_FORMAT.getCode(), "잘못된 요청 형식입니다. 입력값을 확인해 주세요.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolation(DataIntegrityViolationException e) {
        return new ErrorResponse(ErrorCode.DUPLICATE_RESERVATION.getCode(),
                "선택하신 날짜/시간/테마에 이미 예약이 있습니다. 다른 시간을 선택해 주세요.");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAll(Exception e) {
        e.printStackTrace();
        return new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
    }
}
