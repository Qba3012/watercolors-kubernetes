package ogorkiewicz.jakub.mail;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@SpringBootApplication
@EnableAsync
public class MailApplication {

	public static void main(String[] args) {
		SpringApplication.run(MailApplication.class, args);
	}

	// Swagger

	@Bean
	public Docket get() {
		return new Docket(DocumentationType.SWAGGER_2)
			.select()
			.apis(RequestHandlerSelectors.basePackage("ogorkiewicz.jakub.mail.resource"))
			.build()
			.useDefaultResponseMessages(false)
			.apiInfo(createApiInfo());
	}

	private ApiInfo createApiInfo() {
		return new ApiInfoBuilder()
			.title("Watercolors mailing service")
			.version("1.0")
			.contact(new Contact("Jakub Ogórkiewicz", "https://github.com/Qba3012", "jakubogorkiewicz89@gmail.com"))
			.build();
	}

	// Async

	@Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
        return executor;
    }

}