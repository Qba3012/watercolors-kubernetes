package ogorkiewicz.jakub.mail.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends AppException{

    private static final long serialVersionUID = 1L;

    public BadRequestException(ErrorCode errorCode, Class<?> type) {
        super(errorCode, type);
    }
}