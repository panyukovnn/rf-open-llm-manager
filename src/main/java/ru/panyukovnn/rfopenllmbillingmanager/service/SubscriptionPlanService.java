package ru.panyukovnn.rfopenllmbillingmanager.service;

import ru.panyukovnn.rfopenllmbillingmanager.dto.SubscriptionPlanResponse;
import ru.panyukovnn.rfopenllmbillingmanager.model.SubscriptionPlan;
import ru.panyukovnn.referencemodelstarter.dto.response.CommonItemsResponse;

import java.util.List;
import java.util.UUID;

public interface SubscriptionPlanService {

    List<SubscriptionPlan> findAllActivePlans();

    CommonItemsResponse<SubscriptionPlanResponse> findAllActivePlanItems();

    SubscriptionPlan findById(UUID planId);
}
