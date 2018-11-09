package io.micronaut.spring.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import some.other.pkg.FeaturesClient;

@RestController
@RequestMapping("/spring/test")
public class TestController {

    @Autowired
    FeaturesClient featuresClient;

    public TestController() {
        System.out.println("Created test controller");
    }

    @GetMapping
    public String hello() {
        return featuresClient != null ? "good" : "bad";
    }
}
