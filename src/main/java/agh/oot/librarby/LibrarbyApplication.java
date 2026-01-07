package agh.oot.librarby;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "Librarby API",
                version = "1.0",
                description = "Librarby - library management system API documentation"
        )
)


@SpringBootApplication
public class LibrarbyApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibrarbyApplication.class, args);
    }
}
