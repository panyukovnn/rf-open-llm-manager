package ru.panyukovnn.llmrfrouterbillingmanager.service;

import ru.panyukovnn.llmrfrouterbillingmanager.dto.SubscriptionPlanResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.model.SubscriptionPlan;
import ru.panyukovnn.referencemodelstarter.dto.response.CommonItemsResponse;

import java.util.List;
import java.util.UUID;

public interface SubscriptionPlanService {

    List<SubscriptionPlan> findAllActivePlans();

    CommonItemsResponse<SubscriptionPlanResponse> findAllActivePlanItems();

    SubscriptionPlan findById(UUID planId);
}
