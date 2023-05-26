package dynamics.model.repositoies;

import dynamics.model.BaseRepository;
import dynamics.model.entities.Department;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends BaseRepository<Department, Integer>, JpaSpecificationExecutor<Department> {
}
