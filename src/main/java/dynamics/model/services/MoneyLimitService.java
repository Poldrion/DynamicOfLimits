package dynamics.model.services;

import dynamics.model.DynamicsException;
import dynamics.model.entities.Department;
import dynamics.model.entities.MoneyLimit;
import dynamics.model.repositoies.MoneyLimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static dynamics.utils.TitleConstants.*;

@Service
public class MoneyLimitService {

    @Autowired
    private MoneyLimitRepository moneyLimitRepository;

    public Collection<? extends MoneyLimit> findAll() {
        return moneyLimitRepository.findAll();
    }

    public Collection<? extends MoneyLimit> findAllByYearAndNotNullLimit(Integer year) {
        return findAll().stream()
                .filter(moneyLimit -> moneyLimit.getYearLimit() == year)
                .filter(moneyLimit -> moneyLimit.getCost().compareTo(BigDecimal.ZERO) != 0)
                .toList();
    }

    public Collection<? extends MoneyLimit> findAllByYearAndNullLimit(Integer year) {
        return findAll().stream()
                .filter(moneyLimit -> moneyLimit.getYearLimit() == year)
                .filter(moneyLimit -> moneyLimit.getCost().compareTo(BigDecimal.ZERO) == 0)
                .toList();
    }

    public MoneyLimit findMoneyLimitByDepartmentAndYear(Department department, Integer year) {
        List<? extends MoneyLimit> temp = findAll().stream()
                .filter(x -> x.getDepartment().equals(department))
                .filter(x -> x.getYearLimit() == year)
                .toList();
        if (temp.isEmpty()) return null;
        return temp.get(0);
    }

    public BigDecimal getGeneralMoneyLimitByYear(Integer year) {
        List<? extends MoneyLimit> moneyLimitsByYear = findAll().stream()
                .filter(x -> x.getYearLimit() == year)
                .toList();
        if (moneyLimitsByYear.isEmpty()) return BigDecimal.ZERO;

        Function<MoneyLimit, BigDecimal> totalMapper = MoneyLimit::getCost;
        return moneyLimitsByYear.stream().map(totalMapper).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @SuppressWarnings("deprecation")
    public void save(MoneyLimit moneyLimit) {
        if (StringUtils.isEmpty(moneyLimit.getDepartment())) {
            throw new DynamicsException(CREATE_MONEY_LIMIT_EMPTY_DEPARTMENT_ERROR);
        }
        if (StringUtils.isEmpty(moneyLimit.getYearLimit())) {
            throw new DynamicsException(CREATE_MONEY_LIMIT_EMPTY_YEAR_ERROR);
        }
        if (StringUtils.isEmpty(moneyLimit.getCost())) {
            throw new DynamicsException(CREATE_MONEY_LIMIT_EMPTY_COST_ERROR);
        }
        if (validateMoneyLimit(moneyLimit)) moneyLimitRepository.save(moneyLimit);
    }

    @SuppressWarnings("deprecation")
    public void saveChangeLimit(MoneyLimit moneyLimit) {
        if (StringUtils.isEmpty(moneyLimit.getDepartment())) {
            throw new DynamicsException(CREATE_MONEY_LIMIT_EMPTY_DEPARTMENT_ERROR);
        }
        if (StringUtils.isEmpty(moneyLimit.getYearLimit())) {
            throw new DynamicsException(CREATE_MONEY_LIMIT_EMPTY_YEAR_ERROR);
        }
        if (StringUtils.isEmpty(moneyLimit.getCost())) {
            throw new DynamicsException(CREATE_MONEY_LIMIT_EMPTY_COST_ERROR);
        }
        moneyLimitRepository.save(moneyLimit);
    }

    private boolean validateMoneyLimit(MoneyLimit moneyLimit) {
        Collection<? extends MoneyLimit> limits = findAll();
        for (MoneyLimit limit : limits) {
            if (moneyLimit.equals(limit)) {
                throw new DynamicsException(CREATE_MONEY_LIMIT_DUPLICATE_ERROR);
            }
        }
        return true;
    }
}
