package mate.capsharingapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@SQLDelete(sql = "UPDATE cars SET is_deleted = TRUE WHERE id =?")
@SQLRestriction(value = "is_deleted = FALSE")
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String model;
    @Column(nullable = false)
    private String brand;
    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    @Enumerated(EnumType.STRING)
    private CarType carType;
    @Column(nullable = false)
    private int inventory;
    @Column(nullable = false)
    private BigDecimal dailyFee;
    @ToString.Exclude
    @Column(nullable = false)
    private boolean isDeleted = false;

    public enum CarType {
        SEDAN,
        SUV,
        HATCHBACK,
        UNIVERSAL
    }
}
