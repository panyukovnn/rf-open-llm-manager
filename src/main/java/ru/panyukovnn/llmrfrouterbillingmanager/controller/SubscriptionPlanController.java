package ru.panyukovnn.llmrfrouterbillingmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.SubscriptionPlanResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.mapper.SubscriptionMapper;
import ru.panyukovnn.llmrfrouterbillingmanager.model.SubscriptionPlan;
import ru.panyukovnn.llmrfrouterbillingmanager.service.SubscriptionPlanService;
import ru.panyukovnn.referencemodelstarter.dto.response.CommonItemsResponse;
import ru.panyukovnn.referencemodelstarter.dto.response.CommonResponse;

import java.util.List;

@RestController
@RequestMapping("/billing-manager/api/v1/subscription-plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;
    private final SubscriptionMapper subscriptionMapper;

    @GetMapping
    public CommonResponse<CommonItemsResponse<SubscriptionPlanResponse>> findAllActivePlans() {
        List<SubscriptionPlan> plans = subscriptionPlanService.findAllActivePlans();
        List<SubscriptionPlanResponse> planResponses = subscriptionMapper.toSubscriptionPlanResponses(plans);

        CommonItemsResponse<SubscriptionPlanResponse> itemsResponse = CommonItemsResponse.<SubscriptionPlanResponse>builder()
                .items(planResponses)
                .itemsCount(planResponses.size())
                .totalCount(planResponses.size())
                .build();

        return CommonResponse.<CommonItemsResponse<SubscriptionPlanResponse>>builder()
                .data(itemsResponse)
                .build();
    }
}