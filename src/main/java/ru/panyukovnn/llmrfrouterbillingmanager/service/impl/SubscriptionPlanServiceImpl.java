package ru.panyukovnn.llmrfrouterbillingmanager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.panyukovnn.llmrfrouterbillingmanager.model.SubscriptionPlan;
import ru.panyukovnn.llmrfrouterbillingmanager.repository.SubscriptionPlanRepository;
import ru.panyukovnn.llmrfrouterbillingmanager.service.SubscriptionPlanService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @Override
    public List<SubscriptionPlan> findAllActivePlans() {
        return subscriptionPlanRepository.findAllByActiveTrue();
    }

    @Override
    public SubscriptionPlan findById(UUID planId) {
        return subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Тарифный план не найден по идентификатору: " + planId));
    }
}
