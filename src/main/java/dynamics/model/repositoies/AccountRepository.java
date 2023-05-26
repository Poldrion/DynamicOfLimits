package dynamics.model.repositoies;

import dynamics.model.BaseRepository;
import dynamics.model.entities.Account;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AccountRepository extends BaseRepository<Account, String> {
    @Modifying
    @Transactional
    @Query(value = "BACKUP TO ?1", nativeQuery = true)
    int backupDB(String path);
}
