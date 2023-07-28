package dynamics.model.entities;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@Table(name = "Corrections")
public class Corrections {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    private int yearCorrections;
    private LocalDate dateCreate;
    private BigDecimal lastCostLimit;
    private BigDecimal cost;
    private BigDecimal positiveOffset;
    private BigDecimal negativeOffset;
    private BigDecimal currentCostLimit;
    private LocalTime timeCreate;
    private String remark;

    public Corrections() {
        if (cost != null) {
            if (cost.compareTo(BigDecimal.ZERO) < 0) {
                negativeOffset = cost.abs();
                positiveOffset = BigDecimal.ZERO;
            } else {
                positiveOffset = cost;
                negativeOffset = BigDecimal.ZERO;
            }
            currentCostLimit = lastCostLimit.add(cost);
        } else {
            cost = BigDecimal.ZERO;
            positiveOffset = BigDecimal.ZERO;
            negativeOffset = BigDecimal.ZERO;
        }
    }
}

