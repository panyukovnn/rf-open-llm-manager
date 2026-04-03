package ru.panyukovnn.llmrfrouterbillingmanager.exception;

public class TokenLimitExceededException extends RuntimeException {

    public TokenLimitExceededException(String message) {
        super(message);
    }
}
