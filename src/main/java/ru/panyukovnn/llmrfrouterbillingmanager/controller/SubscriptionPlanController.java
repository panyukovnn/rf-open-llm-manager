package ru.panyukovnn.llmrfrouterbillingmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.SubscriptionPlanResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.service.SubscriptionPlanService;
import ru.panyukovnn.referencemodelstarter.dto.response.CommonItemsResponse;
import ru.panyukovnn.referencemodelstarter.dto.response.CommonResponse;

@RestController
@RequestMapping("/api/v1/subscription-plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    @GetMapping
    public CommonResponse<CommonItemsResponse<SubscriptionPlanResponse>> findAllActivePlans() {
        CommonItemsResponse<SubscriptionPlanResponse> items = subscriptionPlanService.findAllActivePlanItems();

        return CommonResponse.<CommonItemsResponse<SubscriptionPlanResponse>>builder()
                .data(items)
                .build();
    }
}
