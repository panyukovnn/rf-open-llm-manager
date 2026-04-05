package ru.panyukovnn.llmrfrouterbillingmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.ApiKeyResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.CreateApiKeyRequest;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.CreateApiKeyResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.service.ApiKeyManager;
import ru.panyukovnn.referencemodelstarter.dto.request.CommonRequest;
import ru.panyukovnn.referencemodelstarter.dto.response.CommonItemsResponse;
import ru.panyukovnn.referencemodelstarter.dto.response.CommonResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyManager apiKeyManager;

    @PostMapping
    public CommonResponse<CreateApiKeyResponse> generateKey(
            @Valid @RequestBody CommonRequest<CreateApiKeyRequest> request) {
        CreateApiKeyResponse response = apiKeyManager.handleGenerateKey(request.getData().getName());

        return CommonResponse.<CreateApiKeyResponse>builder()
                .data(response)
                .build();
    }

    @GetMapping
    public CommonResponse<CommonItemsResponse<ApiKeyResponse>> findUserKeys() {
        List<ApiKeyResponse> keys = apiKeyManager.handleFindUserKeys();

        CommonItemsResponse<ApiKeyResponse> itemsResponse = CommonItemsResponse.<ApiKeyResponse>builder()
                .items(keys)
                .build();

        return CommonResponse.<CommonItemsResponse<ApiKeyResponse>>builder()
                .data(itemsResponse)
                .build();
    }

    @DeleteMapping("/{keyId}")
    public CommonResponse<Void> revokeKey(@PathVariable UUID keyId) {
        apiKeyManager.handleRevokeKey(keyId);

        return CommonResponse.<Void>builder()
                .build();
    }
}
