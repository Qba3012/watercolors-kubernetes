package ogorkiewicz.jakub.images.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    
    NOT_FOUND ("Requested entity not found"),
    ID_NOT_NULL ("Id must be null");

    private String description;

    private ErrorCode(String description) {
        this.description = description;
    }
}
