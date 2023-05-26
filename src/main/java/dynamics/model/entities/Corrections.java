package dynamics.model.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "Corrections")
public class Corrections {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id", nullable = false)
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
    private String remark;


    public Corrections() {
        if()
    }
}
