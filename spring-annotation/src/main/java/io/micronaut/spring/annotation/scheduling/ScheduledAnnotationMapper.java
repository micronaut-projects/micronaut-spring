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
package io.micronaut.spring.annotation.scheduling;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.scheduling.annotation.Scheduled;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

/**
 * Maps {@code @Scheduled} to {@link Scheduled}.
 *
 * @author graemerocher
 * @since 1.0
 */
public class ScheduledAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.scheduling.annotation.Scheduled";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final AnnotationValueBuilder<?> builder = AnnotationValue.builder(Scheduled.class);
        annotation.get("cron", String.class).ifPresent(s -> builder.member("cron", s));

        // members that take numbers needed "ms" appending
        annotation.get("fixedDelay", String.class).ifPresent(s -> builder.member("fixedDelay", s + "ms"));
        annotation.get("fixedRate", String.class).ifPresent(s -> builder.member("fixedRate", s + "ms"));
        annotation.get("initialDelay", String.class).ifPresent(s -> builder.member("initialDelay", s + "ms"));

        annotation.get("fixedDelayString", String.class).ifPresent(s -> builder.member("fixedDelay", s));
        annotation.get("fixedRateString", String.class).ifPresent(s -> builder.member("fixedRate", s));
        annotation.get("initialDelayString", String.class).ifPresent(s -> builder.member("initialDelay", s));

        final AnnotationValue<?> scheduledAnn = builder
                .build();
        return Collections.singletonList(scheduledAnn);
    }
}
