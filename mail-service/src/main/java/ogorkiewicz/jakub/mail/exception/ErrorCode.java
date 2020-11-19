package ogorkiewicz.jakub.mail.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    
    ALREADY_EXIST ("Entity already exist"),
    NOT_EXIST("Requested entity does not exist");

    private String description;

    private ErrorCode(String description) {
        this.description = description;
    }
}
