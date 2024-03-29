package dynamics.model.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "Department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;
    private String name;

    public Department(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
