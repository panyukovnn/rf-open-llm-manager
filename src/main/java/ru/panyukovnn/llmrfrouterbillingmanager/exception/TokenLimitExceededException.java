package ru.panyukovnn.llmrfrouterbillingmanager.exception;

import ru.panyukovnn.referencemodelstarter.exception.BusinessException;

public class TokenLimitExceededException extends BusinessException {

    public TokenLimitExceededException(String location, String displayMessage) {
        super(location, "tokenLimitExceeded", displayMessage);
    }
}