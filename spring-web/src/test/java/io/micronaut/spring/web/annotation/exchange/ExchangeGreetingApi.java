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
package io.micronaut.spring.web.annotation.exchange;

import io.micronaut.http.HttpResponse;
import io.micronaut.spring.web.annotation.Greeting;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

public interface ExchangeGreetingApi {

    @GetExchange
    String home(@Nullable Model model);

    @GetExchange("/withOptVar{/optVar}")
    String withOptVar(@PathVariable(required = false) String optVar);

    @GetExchange("/greeting2")
    Greeting greeting(@RequestParam(value = "name", defaultValue = "World", required = false) @Pattern(regexp = "\\D+") String name);

    @PostExchange("/greeting")
    Greeting greetingByPost(@RequestBody Greeting greeting);

    @DeleteExchange("/greeting")
    HttpResponse<?> deleteGreeting(@RequestHeader(required = false) String myHeader);

    @GetMapping("/greeting-status")
    @ResponseStatus(code = HttpStatus.CREATED)
    Greeting greetingWithStatus(@RequestParam(value = "name", defaultValue = "World") @Pattern(regexp = "\\D+") String name);

    @GetMapping("/nested/greeting{?name}")
    Greeting greetingNested(@RequestParam(value = "name", defaultValue = "World") String name);
}
