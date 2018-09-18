package cz.rumanek.kramerius.krameriusiiif;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class KrameriusiiifApplication {

    @RequestMapping("/")
    public String index() {
        return "Master!";
    }

    public static void main(String[] args) {
        SpringApplication.run(KrameriusiiifApplication.class, args);
    }
}
