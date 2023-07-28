package dynamics;

import dynamics.model.entities.Account;
import dynamics.model.repositoies.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Autowired
    private AccountRepository accountRepository;

    @Bean
    public CommandLineRunner getCommandLineRunner() {
        return args -> {
//            accountRepository.deleteAll();
//
//            Account admin = new Account();
//            admin.setLoginId("admin");
//            admin.setName("admin");
//            admin.setRole(Account.Role.Admin);
//            admin.setPassword("admin");
//
//            accountRepository.save(admin);
//
//            Account user = new Account();
//            user.setLoginId("KomarovAV");
//            user.setName("KomarovAV");
//            user.setRole(Account.Role.Admin);
//            user.setPassword("Forto4kA");
//
//            accountRepository.save(user);
        };
    }
}
