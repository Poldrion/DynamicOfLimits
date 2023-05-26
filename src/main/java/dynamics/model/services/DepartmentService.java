package dynamics.model.services;

import dynamics.model.DynamicsException;
import dynamics.model.entities.Department;
import dynamics.model.entities.MoneyLimit;
import dynamics.model.repositoies.DepartmentRepository;
import dynamics.utils.SpecificationUtils;
import dynamics.views.controllers.MainController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

import static dynamics.utils.TitlesUtils.*;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private MoneyLimitService moneyLimitService;

    public void save(Department department) {
        if (StringUtils.isEmpty(department.getName())) {
            throw new DynamicsException(CREATE_DEPARTMENT_EMPTY_NAME_ERROR);
        }

        if (findByName(department.getName()).isEmpty())
            departmentRepository.save(department);
        else {
            throw new DynamicsException(CREATE_DEPARTMENT_DUPLICATE_ERROR);
        }
    }


    private static Specification<Department> containsText(String text) {
        return (Specification<Department>) SpecificationUtils.containsTextInAttributes(text, Arrays.asList("name"));
    }

    public List<Department> findByName(String name) {
        return departmentRepository.findAll(Specification.where(containsText(name)));
    }


    public Collection<? extends Department> findDepartmentWithLimit() {
        Set<Department> departments = new HashSet<>();
        Integer year = MainController.getYearLimit();
        Collection<? extends MoneyLimit> moneyLimits = moneyLimitService.findAllByYearAndNotNullLimit(year);
        for (MoneyLimit moneyLimit : moneyLimits) {
            departments.add(moneyLimit.getDepartment());
        }
        return departments;
    }

    public Collection<? extends Department> findDepartmentWithoutLimit() {
        Set<Department> departments = new HashSet<>();
        Integer year = MainController.getYearLimit();
        Collection<? extends MoneyLimit> moneyLimits = moneyLimitService.findAllByYearAndNullLimit(year);
        for (MoneyLimit moneyLimit : moneyLimits) {
            departments.add(moneyLimit.getDepartment());
        }
        return departments;
    }


    public Collection<? extends Department> findAll() {
        return departmentRepository.findAll();
    }
}
