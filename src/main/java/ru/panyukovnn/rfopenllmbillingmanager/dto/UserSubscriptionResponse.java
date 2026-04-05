package ru.panyukovnn.rfopenllmbillingmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.panyukovnn.rfopenllmbillingmanager.model.SubscriptionStatus;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscriptionResponse {

    private UUID id;
    private UUID subscriptionPlanId;
    private SubscriptionStatus status;
    private Long tokensUsed;
    private Instant periodStart;
    private Instant periodEnd;
}
