package dynamics.model;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;

public class BaseRepositoryImpl<E, ID> extends SimpleJpaRepository<E, ID> implements BaseRepository<E, ID> {

    private final EntityManager entityManager;

    public BaseRepositoryImpl(JpaEntityInformation<E, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public List<E> findByQuery(String jpql, Map<String, Object> params) {
        TypedQuery<E> query = entityManager.createQuery(jpql, getDomainClass());

        if (params != null) {
            for (String key : params.keySet()) {
                query.setParameter(key, params.get(key));
            }
        }
        return query.getResultList();
    }
}
