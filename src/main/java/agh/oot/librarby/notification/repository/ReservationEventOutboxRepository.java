package agh.oot.librarby.notification.repository;

import agh.oot.librarby.notification.model.ReservationEventOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReservationEventOutboxRepository extends JpaRepository<ReservationEventOutbox, UUID> {

}
