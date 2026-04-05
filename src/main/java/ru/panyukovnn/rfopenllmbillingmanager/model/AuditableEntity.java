package ru.panyukovnn.rfopenllmbillingmanager.model;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.Optional;

import static ru.panyukovnn.rfopenllmbillingmanager.util.Constants.MDC_LOGIN_KEY;

@Getter
@Setter
@MappedSuperclass
public class AuditableEntity {

    public static final String UNDEFINED = "undefined";

    private Instant createTime;
    private String createUser;
    private Instant lastUpdateTime;
    private String lastUpdateUser;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        String login = Optional.ofNullable(MDC.get(MDC_LOGIN_KEY))
                .orElse(UNDEFINED);

        this.createTime = now;
        this.lastUpdateTime = now;
        this.createUser = login;
        this.lastUpdateUser = login;
    }

    @PreUpdate
    public void preUpdate() {
        String login = Optional.ofNullable(MDC.get(MDC_LOGIN_KEY))
                .orElse(UNDEFINED);

        this.lastUpdateTime = Instant.now();
        this.lastUpdateUser = login;

        // На случай если id задается вручную и @PrePersist не отработает
        if (this.createTime == null) {
            this.createTime = Instant.now();
        }

        if (this.createUser == null) {
            this.createUser = login;
        }
    }
}
