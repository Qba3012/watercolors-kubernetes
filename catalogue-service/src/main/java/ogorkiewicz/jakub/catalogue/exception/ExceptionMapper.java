package ogorkiewicz.jakub.catalogue.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import feign.FeignException;
import lombok.extern.jbosslog.JBossLog;

@ControllerAdvice
@JBossLog
public class ExceptionMapper {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    public ErrorResponse handleBadRequests(BadRequestException e) {
        return new ErrorResponse(e);
    }
    
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public ErrorResponse handleServerError(ServiceException e) {
        return new ErrorResponse(e);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(FeignException.class)
    @ResponseBody
    public ErrorResponse handleFeignExceptions(FeignException e) {
        try {
            return new ObjectMapper().readValue(e.responseBody().get().array(), ErrorResponse.class);
        } catch (IOException ex) {
            log.error("Unknown bad request feign response. " + ex.getMessage());
            throw new ServiceException(ErrorCode.SERVICE_NOT_READY, FeignClient.class);
        }
    }

}