package dynamics.model.repositoies;

import dynamics.model.BaseRepository;
import dynamics.model.entities.Corrections;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CorrectionsRepository extends BaseRepository<Corrections, Long>, JpaSpecificationExecutor<Corrections> {
}
