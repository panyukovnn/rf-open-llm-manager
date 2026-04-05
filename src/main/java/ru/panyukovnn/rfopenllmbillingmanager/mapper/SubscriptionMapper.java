package ru.panyukovnn.rfopenllmbillingmanager.mapper;

import org.mapstruct.Mapper;
import ru.panyukovnn.rfopenllmbillingmanager.dto.SubscriptionPlanResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.UserSubscriptionResponse;
import ru.panyukovnn.rfopenllmbillingmanager.model.SubscriptionPlan;
import ru.panyukovnn.rfopenllmbillingmanager.model.UserSubscription;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    SubscriptionPlanResponse toSubscriptionPlanResponse(SubscriptionPlan subscriptionPlan);

    List<SubscriptionPlanResponse> toSubscriptionPlanResponses(List<SubscriptionPlan> subscriptionPlans);

    UserSubscriptionResponse toUserSubscriptionResponse(UserSubscription userSubscription);
}
