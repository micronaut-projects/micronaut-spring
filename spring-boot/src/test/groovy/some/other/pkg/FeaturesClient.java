package some.other.pkg;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import some.other.pkg.FeaturesEndpoint;

import java.util.Map;

@Client("/features")
public interface FeaturesClient {


    @Get("/")
    Map<String, FeaturesEndpoint.Feature> features();

    @Get("/{name}")
    FeaturesEndpoint.Feature features(String name);

    @Delete("/{name}")
    HttpStatus deleteFeature(String name);

    @Post("/{name}")
    HttpStatus saveFeature(String name, FeaturesEndpoint.Feature feature);
}
