package ru.panyukovnn.rfopenllmbillingmanager.service;

import ru.panyukovnn.rfopenllmbillingmanager.dto.ChatMessage;
import ru.panyukovnn.rfopenllmbillingmanager.model.Message;
import ru.panyukovnn.rfopenllmbillingmanager.model.Session;

import java.util.List;

public interface ContextBuilder {

    List<ChatMessage> build(Session session, List<Message> history, String userContent);
}
