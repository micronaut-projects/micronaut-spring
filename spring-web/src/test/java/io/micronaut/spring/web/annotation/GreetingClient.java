/*
 * Copyright 2017-2019 original authors
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

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.multipart.MultipartBody;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Client("/")
public interface GreetingClient {

    @GetMapping("/")
    String home();

    @PostMapping("/request")
    @Header(name = "Foo", value = "Bar")
    Greeting requestTest(@RequestBody Greeting greeting);

    @GetMapping("/greeting{?name}")
    Greeting greet(@Nullable String name);

    @PostMapping("/greeting")
    Greeting greetByPost(@RequestBody Greeting greeting);

    @DeleteMapping("/greeting")
    HttpResponse<?> deletePost();

    @GetMapping("/nested/greeting{?name}")
    Greeting nestedGreet(@Nullable String name);

    @GetMapping("/greeting-status{?name}")
    HttpResponse<Greeting> greetWithStatus(@Nullable String name);

    @PostMapping(value = "/multipart-request", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    String multipartRequest(@RequestBody MultipartBody body);
}
