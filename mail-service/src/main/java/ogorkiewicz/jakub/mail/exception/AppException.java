package ogorkiewicz.jakub.mail.exception;

import lombok.Getter;

@Getter
public abstract class AppException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private ErrorCode errorCode;
    private Class<?> type;

    public AppException (ErrorCode errorCode, Class<?> type) {
        this.errorCode = errorCode;
        this.type = type;
    }
}