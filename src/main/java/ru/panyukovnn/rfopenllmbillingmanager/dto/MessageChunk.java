package ru.panyukovnn.rfopenllmbillingmanager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageChunk {

    /**
     * Тип чанка — TOKEN/DONE/ERROR
     */
    private MessageChunkType type;
    /**
     * Содержимое чанка — токен, сообщение об ошибке или пусто для DONE
     */
    private String content;
    /**
     * Идентификатор сообщения ассистента — заполнен в DONE-чанке
     */
    private UUID messageId;
}
