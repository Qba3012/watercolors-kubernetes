package ogorkiewicz.jakub.catalogue.exception;

import lombok.Getter;

@Getter
public class ServiceException extends AppException{

    private static final long serialVersionUID = 1L;

    public ServiceException (ErrorCode errorCode, Class<?> type) {
        super(errorCode, type);
    }
}