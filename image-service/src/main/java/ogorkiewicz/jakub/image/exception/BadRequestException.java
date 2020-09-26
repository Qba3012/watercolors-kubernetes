package ogorkiewicz.jakub.image.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    
    private ErrorCode errorCode;
    private Class<?> type;

    public BadRequestException (ErrorCode errorCode, Class<?> type) {
        this.errorCode = errorCode;
        this.type = type;
    }
}
