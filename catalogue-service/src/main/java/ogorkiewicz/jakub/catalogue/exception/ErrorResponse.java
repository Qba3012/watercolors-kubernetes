package ogorkiewicz.jakub.catalogue.exception;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class ErrorResponse {
    
    String errorCode;
    String message;
    
    public ErrorResponse(AppException e) {
        this.errorCode = e.getType().getSimpleName() + "." + e.getErrorCode();
        this.message = e.getErrorCode().getDescription();
    }

}