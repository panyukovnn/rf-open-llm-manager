package ru.panyukovnn.rfopenllmbillingmanager.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.panyukovnn.rfopenllmbillingmanager.dto.SubscriptionPlanResponse;
import ru.panyukovnn.rfopenllmbillingmanager.mapper.SubscriptionMapper;
import ru.panyukovnn.rfopenllmbillingmanager.model.SubscriptionPlan;
import ru.panyukovnn.rfopenllmbillingmanager.repository.SubscriptionPlanRepository;
import ru.panyukovnn.rfopenllmbillingmanager.service.impl.SubscriptionPlanServiceImpl;
import ru.panyukovnn.referencemodelstarter.dto.response.CommonItemsResponse;
import ru.panyukovnn.referencemodelstarter.exception.BusinessException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionPlanServiceImplUnitTest {

    @Mock
    private SubscriptionPlanRepository subscriptionPlanRepository;
    @Mock
    private SubscriptionMapper subscriptionMapper;

    @InjectMocks
    private SubscriptionPlanServiceImpl subscriptionPlanService;

    @Nested
    class FindAllActivePlans {

        @Test
        void when_findAllActivePlans_then_success() {
            SubscriptionPlan plan = SubscriptionPlan.builder()
                    .id(UUID.randomUUID())
                    .name("Free")
                    .active(true)
                    .build();

            when(subscriptionPlanRepository.findAllByActiveTrue())
                    .thenReturn(List.of(plan));

            List<SubscriptionPlan> result = subscriptionPlanService.findAllActivePlans();

            assertEquals(1, result.size());
            assertEquals("Free", result.get(0).getName());
        }
    }

    @Nested
    class FindAllActivePlanItems {

        @Test
        void when_findAllActivePlanItems_then_success() {
            UUID planId = UUID.randomUUID();
            SubscriptionPlan plan = SubscriptionPlan.builder()
                    .id(planId)
                    .name("Free")
                    .active(true)
                    .build();
            SubscriptionPlanResponse planResponse = SubscriptionPlanResponse.builder()
                    .id(planId)
                    .name("Free")
                    .build();

            when(subscriptionPlanRepository.findAllByActiveTrue())
                    .thenReturn(List.of(plan));
            when(subscriptionMapper.toSubscriptionPlanResponses(List.of(plan)))
                    .thenReturn(List.of(planResponse));

            CommonItemsResponse<SubscriptionPlanResponse> result = subscriptionPlanService.findAllActivePlanItems();

            assertEquals(1, result.getItemsCount());
            assertEquals(1, result.getTotalCount());
            assertEquals("Free", result.getItems().get(0).getName());
            verify(subscriptionMapper).toSubscriptionPlanResponses(List.of(plan));
        }
    }

    @Nested
    class FindById {

        @Test
        void when_findById_then_success() {
            UUID planId = UUID.randomUUID();
            SubscriptionPlan plan = SubscriptionPlan.builder()
                    .id(planId)
                    .name("Standard")
                    .build();

            when(subscriptionPlanRepository.findById(planId))
                    .thenReturn(Optional.of(plan));

            SubscriptionPlan result = subscriptionPlanService.findById(planId);

            assertEquals("Standard", result.getName());
        }

        @Test
        void when_findById_withNonExistentPlan_then_throwsException() {
            UUID planId = UUID.randomUUID();

            when(subscriptionPlanRepository.findById(planId))
                    .thenReturn(Optional.empty());

            assertThrows(BusinessException.class,
                    () -> subscriptionPlanService.findById(planId));
        }
    }
}
