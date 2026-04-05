package ru.panyukovnn.llmrfrouterbillingmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.UsageSummaryResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.service.UsageManager;
import ru.panyukovnn.referencemodelstarter.dto.response.CommonResponse;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/usage")
@RequiredArgsConstructor
public class UsageController {

    private final UsageManager usageManager;

    @GetMapping
    public CommonResponse<UsageSummaryResponse> findUsageSummary(
            @RequestParam Instant from,
            @RequestParam Instant to) {
        UsageSummaryResponse summary = usageManager.handleFindCurrentUserUsageSummary(from, to);

        return CommonResponse.<UsageSummaryResponse>builder()
                .data(summary)
                .build();
    }
}
