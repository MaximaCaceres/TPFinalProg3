package utn.crud.tpfinal;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableCaching
@EnableJpaRepositories(basePackages = "utn.crud.tpfinal.Repository.JPA") //Crea una subcarpeta 'jpa' para tus repositorios JPA

@EnableMongoRepositories(basePackages = "utn.crud.tpfinal.Repository.MONGO") //Crea una subcarpeta 'mongo' para tus repositorios Mongo
@SpringBootApplication
public class TpFinalApplication {

    public static void main(String[] args) {
        SpringApplication.run(TpFinalApplication.class, args);
    }

}
