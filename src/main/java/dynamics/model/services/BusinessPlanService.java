package dynamics.model.services;

import dynamics.model.DynamicsException;
import dynamics.model.entities.BusinessPlan;
import dynamics.model.entities.Department;
import dynamics.model.repositoies.BusinessPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;

import static dynamics.utils.TitlesUtils.*;

@Service
public class BusinessPlanService {

    @Autowired
    private BusinessPlanRepository businessPlanRepository;

    public Collection<? extends BusinessPlan> findAll() {
        return businessPlanRepository.findAll();
    }

    public Collection<? extends BusinessPlan> findAllByYear(Integer year) {
        return findAll().stream()
                .filter(businessPlan -> businessPlan.getYearBP() == year)
                .toList();
    }

    public BusinessPlan getBusinessPlanByDepartmentAndYear(Department department, Integer year) {
        List<? extends BusinessPlan> temp = findAll().stream()
                .filter(x -> x.getDepartment().equals(department))
                .filter(x -> x.getYearBP() == year)
                .toList();
        if (temp.isEmpty()) return null;
        return temp.get(0);
    }

    public void save(BusinessPlan businessPlan) {
         if (StringUtils.isEmpty(businessPlan.getDepartment())) {
            throw new DynamicsException(CREATE_BUSINESS_PLAN_EMPTY_DEPARTMENT_ERROR);
        }
        if (StringUtils.isEmpty(businessPlan.getYearBP())) {
            throw new DynamicsException(CREATE_BUSINESS_PLAN_EMPTY_YEAR_ERROR);
        }
        if (StringUtils.isEmpty(businessPlan.getCost())) {
            throw new DynamicsException(CREATE_BUSINESS_PLAN_EMPTY_COST_ERROR);
        }
        if (validateBusinessPlan(businessPlan)) businessPlanRepository.save(businessPlan);
    }

    public void saveChangeBusinessPlan(BusinessPlan businessPlan) {
        if (StringUtils.isEmpty(businessPlan.getDepartment())) {
            throw new DynamicsException(CREATE_BUSINESS_PLAN_EMPTY_DEPARTMENT_ERROR);
        }
        if (StringUtils.isEmpty(businessPlan.getYearBP())) {
            throw new DynamicsException(CREATE_BUSINESS_PLAN_EMPTY_YEAR_ERROR);
        }
        if (StringUtils.isEmpty(businessPlan.getCost())) {
            throw new DynamicsException(CREATE_BUSINESS_PLAN_EMPTY_COST_ERROR);
        }
        businessPlanRepository.save(businessPlan);
    }

    private boolean validateBusinessPlan(BusinessPlan businessPlan) {
        Collection<? extends BusinessPlan> businessPlans = findAll();
        for (BusinessPlan plan : businessPlans) {
            if (businessPlan.equals(plan)) {
                throw new DynamicsException(CREATE_BUSINESS_PLAN_DUPLICATE_ERROR);
            }
        }
        return true;
    }
}
