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
