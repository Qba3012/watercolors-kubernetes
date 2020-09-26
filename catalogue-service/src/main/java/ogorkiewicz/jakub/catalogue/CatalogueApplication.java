package ogorkiewicz.jakub.catalogue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableSwagger2
public class CatalogueApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatalogueApplication.class, args);
	}

	@Bean
	public Docket get() {
		return new Docket(DocumentationType.SWAGGER_2)
			.select()
			.apis(RequestHandlerSelectors.basePackage("ogorkiewicz.jakub.catalogue.resource"))
			.build()
			.useDefaultResponseMessages(false)
			.apiInfo(createApiInfo());
	}

	private ApiInfo createApiInfo() {
		return new ApiInfoBuilder()
			.title("Watercolors catalogue")
			.description("CRUD endpoints for watercolors catalogue microservice")
			.version("1.0")
			.contact(new Contact("Jakub Ogórkiewicz", "https://github.com/Qba3012", "jakubogorkiewicz89@gmail.com"))
			.build();
	}

}
