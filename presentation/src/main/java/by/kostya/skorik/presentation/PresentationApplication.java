package by.kostya.skorik.presentation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = "by.kostya.skorik")
/*
Это происходит потому, что репозитории Spring Data JPA требуют отдельной активации,
если они находятся не в том же пакете, что и основной @SpringBootApplication
 */
@EnableJpaRepositories(basePackages = "by.kostya.skorik.persistence.repository")
@EntityScan("by.kostya.skorik.persistence.entity")
@EnableScheduling
public class PresentationApplication {

    public static void main(String[] args) {
        SpringApplication.run(PresentationApplication.class, args);
    }

}
