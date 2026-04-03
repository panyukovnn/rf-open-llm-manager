package ru.panyukovnn.llmrfrouterbillingmanager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.SubscriptionPlanResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.mapper.SubscriptionMapper;
import ru.panyukovnn.llmrfrouterbillingmanager.model.SubscriptionPlan;
import ru.panyukovnn.llmrfrouterbillingmanager.repository.SubscriptionPlanRepository;
import ru.panyukovnn.llmrfrouterbillingmanager.service.SubscriptionPlanService;
import ru.panyukovnn.referencemodelstarter.dto.response.CommonItemsResponse;

import ru.panyukovnn.referencemodelstarter.exception.BusinessException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Override
    public List<SubscriptionPlan> findAllActivePlans() {
        return subscriptionPlanRepository.findAllByActiveTrue();
    }

    @Override
    public CommonItemsResponse<SubscriptionPlanResponse> findAllActivePlanItems() {
        List<SubscriptionPlan> plans = findAllActivePlans();
        List<SubscriptionPlanResponse> planResponses = subscriptionMapper.toSubscriptionPlanResponses(plans);

        return CommonItemsResponse.<SubscriptionPlanResponse>builder()
                .items(planResponses)
                .itemsCount(planResponses.size())
                .totalCount(planResponses.size())
                .build();
    }

    @Override
    public SubscriptionPlan findById(UUID planId) {
        return subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new BusinessException(
                        "e7a3",
                        "Тарифный план не найден"));
    }
}