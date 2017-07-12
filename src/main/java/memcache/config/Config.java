package memcache.config;

import io.swagger.annotations.Api;
import memcache.service.AddressCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.concurrent.TimeUnit;


@Configuration
@EnableSwagger2
public class Config {

    @Value("${max.age.of.entry:5}")
    int maxAge;

    @Value("${time.unit:seconds}")
    String unit;

    @Bean
    public Docket customImplementation() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .build()
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false);
    }

    @Bean
    public AddressCache getAddressCache() {
        return new AddressCache(maxAge, TimeUnit.valueOf(unit.toUpperCase()));
    }

    private ApiInfo apiInfo() {

        String title = "Memory cache API";
        String description = "Provides handles to Cache data";
        String version = "1.0";
        String termsOfServiceUrl = "";
        String contact = "chanakya.ajith.com";
        String license = "";
        String licenseUrl = "";
        return new ApiInfo(
                title,
                description,
                version,
                termsOfServiceUrl,
                contact,
                license,
                licenseUrl
        );
    }

}
