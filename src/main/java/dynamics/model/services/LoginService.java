package dynamics.model.services;

import dynamics.model.DynamicsException;
import dynamics.model.entities.Account;
import dynamics.model.repositoies.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
public class LoginService {

    @Autowired
    private AccountRepository accountRepository;

    public Account login(String loginId, String password) {

        if (StringUtils.isEmpty(loginId)) {
            throw new DynamicsException("Пожалуйста, введите имя пользователя");
        }

        if (StringUtils.isEmpty(password)) {
            throw new DynamicsException("Пожалуйста, введите пароль");
        }

        Account account = accountRepository.findById(loginId).
                orElseThrow(() -> new DynamicsException("Пожалуйста, проверьте Ваше имя пользователя."));

        if (!password.equals(account.getPassword())){
            throw new DynamicsException("Пожалуйста, проверьте Ваш пароль.");
        }

        return account;
    }


}
