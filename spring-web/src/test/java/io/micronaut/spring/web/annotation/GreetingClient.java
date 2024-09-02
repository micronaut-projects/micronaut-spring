/*
 * Copyright 2017-2024 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.spring.web.annotation;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.multipart.MultipartBody;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Client("/")
public interface GreetingClient {

    @GetMapping("/")
    String home();

    @PostMapping("/request")
    @Header(name = "Foo", value = "Bar")
    Greeting requestTest(@RequestBody Greeting greeting);

    @GetMapping("/withOptVar{/optVar}")
    String withOptVar(@PathVariable(required = false) String optVar);

    @GetMapping("/greeting{?name}")
    Greeting greet(@RequestParam(required = false) String name);

    @PostMapping("/greeting")
    Greeting greetByPost(@RequestBody Greeting greeting);

    @DeleteMapping("/greeting")
    HttpResponse<?> deletePost(@RequestHeader(required = false) String myHeader);

    @GetMapping("/nested/greeting{?name}")
    Greeting nestedGreet(@RequestParam(required = false) String name);

    @GetMapping("/greeting-status{?name}")
    HttpResponse<Greeting> greetWithStatus(@RequestParam(required = false) String name);

    @PostMapping(value = "/multipart-request", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    String multipartRequest(@RequestBody MultipartBody body);
}
