package com.pharm.pharmavigil_platform.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface StringMapper {

    @Named("trim")
    default String trim(String value) {
        return value != null ? value.trim() : "";
    }
}
