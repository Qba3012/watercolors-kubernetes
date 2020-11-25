package ogorkiewicz.jakub.catalogue.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    
    NOT_FOUND ("Requested entity not found"),
    SERVICE_NOT_READY("Unexpected server problem. Try again later."),
    ID_NOT_NULL ("Id must be null");

    private String description;

    private ErrorCode(String description) {
        this.description = description;
    }
}
