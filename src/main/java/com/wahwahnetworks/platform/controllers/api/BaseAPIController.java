package com.wahwahnetworks.platform.controllers.api;

import com.newrelic.api.agent.NewRelic;
import com.wahwahnetworks.platform.exceptions.EntityNotFoundException;
import com.wahwahnetworks.platform.exceptions.ModelValidationException;
import com.wahwahnetworks.platform.exceptions.ServiceException;
import com.wahwahnetworks.platform.models.web.ExceptionInfoModel;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by jhaygood on 11/14/15.
 */
public class BaseAPIController {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handle(HttpMessageNotReadableException e) {
        Logger log = Logger.getLogger(this.getClass());
        log.error("Returning HTTP 400 Bad Request", e);
        NewRelic.noticeError(e);
        throw e;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionInfoModel> handleExceptions(HttpServletRequest request, Exception exception)
    {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if (exception instanceof ModelValidationException)
        {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        else if (exception instanceof EntityNotFoundException)
        {
            httpStatus = HttpStatus.NOT_FOUND;
        }
        else if (exception instanceof ServiceException)
        {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        ExceptionInfoModel exceptionInfo = new ExceptionInfoModel();
        exceptionInfo.setUrl(request.getRequestURL().toString());
        exceptionInfo.setStatus(httpStatus.value());
        exceptionInfo.setMessage(exception.getMessage());

        Logger log = Logger.getLogger(this.getClass());
        log.error(exceptionInfo, exception);
        NewRelic.noticeError(exception);

        return new ResponseEntity<>(exceptionInfo, httpStatus);
    }
}
