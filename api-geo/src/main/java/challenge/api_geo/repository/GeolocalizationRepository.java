package challenge.api_geo.repository;

import challenge.api_geo.entity.GeolocalizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GeolocalizationRepository extends JpaRepository<GeolocalizationEntity, UUID> {
}