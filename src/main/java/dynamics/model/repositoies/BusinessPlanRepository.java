package dynamics.model.repositoies;

import dynamics.model.BaseRepository;
import dynamics.model.entities.BusinessPlan;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessPlanRepository extends BaseRepository<BusinessPlan, Long>, JpaSpecificationExecutor<BusinessPlan> {
}
