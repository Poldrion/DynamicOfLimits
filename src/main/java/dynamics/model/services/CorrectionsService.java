package dynamics.model.services;

import dynamics.model.DynamicsException;
import dynamics.model.entities.Corrections;
import dynamics.model.entities.Department;
import dynamics.model.repositoies.BusinessPlanRepository;
import dynamics.model.repositoies.CorrectionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;

import static dynamics.utils.TitleConstants.*;

@Service
public class CorrectionsService {

    @Autowired
    private CorrectionsRepository correctionsRepository;
    @Autowired
    private BusinessPlanRepository businessPlanRepository;

    @SuppressWarnings("deprecation")
    public void save(Corrections correction) {
        if (StringUtils.isEmpty(correction.getDepartment())) {
            throw new DynamicsException(CORRECTIONS_EMPTY_DEPARTMENT_ERROR);
        }
        if (StringUtils.isEmpty(correction.getYearCorrections())) {
            throw new DynamicsException(CORRECTIONS_EMPTY_YEAR_ERROR);
        }
        if (StringUtils.isEmpty(correction.getDateCreate())) {
            throw new DynamicsException(CORRECTIONS_EMPTY_DATE_CREATE_ERROR);
        }
        if (StringUtils.isEmpty(correction.getLastCostLimit())) {
            throw new DynamicsException(CORRECTIONS_EMPTY_LAST_COST_LIMIT_ERROR);
        }
        if (StringUtils.isEmpty(correction.getCost())) {
            throw new DynamicsException(CORRECTIONS_EMPTY_COST_ERROR);
        }
        if (StringUtils.isEmpty(correction.getCurrentCostLimit())) {
            throw new DynamicsException(CORRECTIONS_EMPTY_CURRENT_COST_LIMIT_ERROR);
        }
        if (StringUtils.isEmpty(correction.getRemark())) {
            throw new DynamicsException(CORRECTIONS_EMPTY_REMARK_ERROR);
        }

        correctionsRepository.save(correction);
    }

    public Collection<? extends Corrections> findByDepartmentAndYear(Department department, Integer year) {
        return findAll().stream()
                .filter(x -> x.getDepartment().equals(department))
                .filter(x -> x.getYearCorrections() == year)
                .toList();
    }

    public Collection<? extends Corrections> findAll() {
        return correctionsRepository.findAll();
    }

    public void delete(Long id) {
        correctionsRepository.deleteById(id);
    }

    public int getCountCorrectionsByDepartmentAndYear(Department department, int year) {
        return findByDepartmentAndYear(department, year).size() - 1;
    }
}
