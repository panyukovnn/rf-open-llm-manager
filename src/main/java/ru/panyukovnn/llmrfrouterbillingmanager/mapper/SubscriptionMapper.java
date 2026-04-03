package ru.panyukovnn.llmrfrouterbillingmanager.mapper;

import org.mapstruct.Mapper;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.SubscriptionPlanResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.UserSubscriptionResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.model.SubscriptionPlan;
import ru.panyukovnn.llmrfrouterbillingmanager.model.UserSubscription;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    SubscriptionPlanResponse toSubscriptionPlanResponse(SubscriptionPlan subscriptionPlan);

    List<SubscriptionPlanResponse> toSubscriptionPlanResponses(List<SubscriptionPlan> subscriptionPlans);

    UserSubscriptionResponse toUserSubscriptionResponse(UserSubscription userSubscription);
}
