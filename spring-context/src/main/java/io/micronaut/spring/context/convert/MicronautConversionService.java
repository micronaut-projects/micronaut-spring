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
package io.micronaut.spring.context.convert;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named("conversionService")
public class MicronautConversionService implements ConversionService {

    private final io.micronaut.core.convert.ConversionService<?> conversionService;

    public MicronautConversionService(io.micronaut.core.convert.ConversionService<?> conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
        return conversionService.canConvert(sourceType, targetType);
    }

    @Override
    public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return conversionService.canConvert(sourceType.getType(), targetType.getType());
    }

    @Override
    public <T> T convert(Object source, Class<T> targetType) {
        return conversionService.convert(source, targetType).orElse(null);
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        return conversionService.convert(source.getClass(), targetType.getType()).orElse(null);
    }
}
