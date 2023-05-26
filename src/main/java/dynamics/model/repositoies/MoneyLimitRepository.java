package dynamics.model.repositoies;

import dynamics.model.BaseRepository;
import dynamics.model.entities.MoneyLimit;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MoneyLimitRepository extends BaseRepository<MoneyLimit, Long>, JpaSpecificationExecutor<MoneyLimit> {
}
