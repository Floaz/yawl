package org.yawlfoundation.yawl.resourcing.exception;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;



@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDetail handleBadRequestException(BadRequestException ex){
        return new ErrorDetail(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }


    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorDetail handleConflictException(ConflictException ex){
        return new ErrorDetail(HttpStatus.CONFLICT.value(), ex.getMessage());
    }


    @ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
	public ErrorDetail handleNotFoundException(NotFoundException ex){
		return new ErrorDetail(HttpStatus.NOT_FOUND.value(), ex.getMessage());
	}


    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDetail handleNoHandlerFoundException(NoHandlerFoundException ex){
        return new ErrorDetail(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }


    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorDetail handleAccessDeniedException(AccessDeniedException ex){
        return new ErrorDetail(HttpStatus.FORBIDDEN.value(), ex.getMessage());
    }


	@ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
	public ErrorDetail handleException(Exception ex){
        logger.error("Internal Server Error while handling request. ", ex);
		return new ErrorDetail(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
	}

}