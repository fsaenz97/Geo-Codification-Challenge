package challenge.api_geo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "geolocalizations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeolocalizationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String province;

    @Column(nullable = false)
    private String country;

    private Double latitude;

    private Double longitude;

    @Column(nullable = false)
    private String status;
}