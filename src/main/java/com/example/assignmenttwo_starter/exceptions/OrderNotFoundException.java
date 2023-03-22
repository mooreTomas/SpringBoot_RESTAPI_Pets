package com.example.assignmenttwo_starter.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="order Not Found")
public class OrderNotFoundException extends RuntimeException {

}
