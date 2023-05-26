package dynamics.model.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Data
@Entity
@NoArgsConstructor
@Table(name = "MoneyLimit")
public class MoneyLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private int yearLimit;
    private BigDecimal cost = null;
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoneyLimit that = (MoneyLimit) o;
        return yearLimit == that.yearLimit && department.equals(that.department);
    }

    @Override
    public int hashCode() {
        return Objects.hash(yearLimit, department);
    }
}
