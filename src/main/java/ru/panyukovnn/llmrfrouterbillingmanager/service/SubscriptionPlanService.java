package ru.panyukovnn.llmrfrouterbillingmanager.service;

import ru.panyukovnn.llmrfrouterbillingmanager.model.SubscriptionPlan;

import java.util.List;
import java.util.UUID;

public interface SubscriptionPlanService {

    List<SubscriptionPlan> findAllActivePlans();

    SubscriptionPlan findById(UUID planId);
}
