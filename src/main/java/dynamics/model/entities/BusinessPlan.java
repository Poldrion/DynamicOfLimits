package dynamics.model.entities;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Data
@NoArgsConstructor
@Entity
@Table(name = "BusinessPlan")
public class BusinessPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    private int yearBP;
    private BigDecimal cost;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusinessPlan that = (BusinessPlan) o;
        return yearBP == that.yearBP && department.equals(that.department);
    }

    @Override
    public int hashCode() {
        return Objects.hash(department, yearBP);
    }
}
