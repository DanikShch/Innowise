package com.innowise.userservice.exception;

public class CardNumberAlreadyExistsException extends RuntimeException {
    public CardNumberAlreadyExistsException(String cardNumber) {
        super("Card number already exists: " + cardNumber);
    }
}
