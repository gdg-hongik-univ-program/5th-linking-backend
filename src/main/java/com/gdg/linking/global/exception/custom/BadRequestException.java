package com.gdg.linking.global.exception.custom;

public class BadRequestException extends RuntimeException {
  public BadRequestException(String message) {
      super(message);
  }
}
