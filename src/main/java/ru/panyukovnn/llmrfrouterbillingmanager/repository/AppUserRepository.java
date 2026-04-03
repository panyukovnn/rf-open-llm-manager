package ru.panyukovnn.llmrfrouterbillingmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.panyukovnn.llmrfrouterbillingmanager.model.AppUser;

import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    Optional<AppUser> findByGoogleId(String googleId);

    Optional<AppUser> findByEmail(String email);
}
