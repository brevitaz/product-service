package com.brevitaz.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.stereotype.Component;

@Component
public class ProductObjectMapper extends ObjectMapper{
    public ProductObjectMapper(){
        super();
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public FilterProvider ignoreFieldsFilter(String... fields){
        SimpleBeanPropertyFilter theFilter = SimpleBeanPropertyFilter.serializeAllExcept(fields);
        FilterProvider filters = new SimpleFilterProvider().addFilter("propFilter", theFilter);
        return filters;
    }
}
