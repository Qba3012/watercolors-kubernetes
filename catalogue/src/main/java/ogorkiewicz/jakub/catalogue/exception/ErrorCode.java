package ogorkiewicz.jakub.catalogue.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    
    NOT_FOUND ("Requested entity not found");

    private String description;

    private ErrorCode(String description) {
        this.description = description;
    }
}
