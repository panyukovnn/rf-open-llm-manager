package ru.panyukovnn.llmrfrouterbillingmanager.exception;

import ru.panyukovnn.referencemodelstarter.exception.BusinessException;

public class NoActiveSubscriptionException extends BusinessException {

    public NoActiveSubscriptionException(String location, String displayMessage) {
        super(location, "noActiveSubscription", displayMessage);
    }
}
