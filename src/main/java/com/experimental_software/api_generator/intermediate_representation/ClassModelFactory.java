package com.experimental_software.api_generator.intermediate_representation;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import com.experimental_software.api_generator.openehr_element.Attribute;
import com.experimental_software.api_generator.openehr_element.ClassModel;
import com.experimental_software.api_generator.openehr_element.Function;
import com.experimental_software.api_generator.openehr_element.Multiplicity;
import com.experimental_software.api_generator.openehr_element.Parameter;
import com.experimental_software.api_generator.openehr_element.Type;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("unchecked")
public class ClassModelFactory {

    private final IR ir;

    public ClassModelFactory(String json) {
        try {
            var objectMapper = new ObjectMapper();
            ir = objectMapper.readValue(json, IR.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ClassModel create() {
        return ClassModel.builder()
            .name(ir.meta_info().name())
            .description(ir.meta_info().description())
            .baseTypes(
                ir.meta_info().base_types() != null ?
                    ir.meta_info().base_types().stream().map(Type::new).collect(toList())
                    : null
            )
            .attributes(getAttributes())
            .functions(getFunctions())
            .build();
    }

    private List<Function> getFunctions() {
        List<Function> result = new ArrayList<>();

        for (var function : ir.functions()) {
            result.add(
              Function.builder()
                  .name(function.name())
                  .description(function.meaning())
                  .parameters(
                      function.parameters().stream().map(
                          p -> Parameter.builder()
                              .name(p.name())
                              .type(new Type(p.type()))
                              .build()
                          )
                          .collect(toList())
                  )
                  .returnType(new Type(function.return_type()))
                  .build()
            );
        }
        return result;
    }

    private List<Attribute> getAttributes() {
        List<Attribute> result = new ArrayList<>();
        for (var attribute : ir.attributes()) {
            var type = attribute.type();
            if (type == null) {
                var msg = String.format("Could not find type for attribute with name '%s'.", attribute.name());
                log.warn(msg);
                // TODO: Raise exception if running in strict mode, because this state indicates incomplete parsing
                continue;
            }

            result.add(
                Attribute.builder()
                    .description(attribute.description())
                    .name(attribute.name())
                    .multiplicity(parseMultiplicity(attribute.multiplicity()))
                    .type(new Type(type))
                    .build()
            );
        }
        return result;
    }

    private static Multiplicity parseMultiplicity(String multiplicityRaw) {
        if (multiplicityRaw == null) {
            return Multiplicity.ONE;
        }

        return switch (multiplicityRaw) {
            case "0..1" -> Multiplicity.ZERO_OR_ONE;
            case "1..1" -> Multiplicity.ONE;
            case "0..*" -> Multiplicity.MANY;
            default -> throw new IllegalArgumentException("Unknown multiplicity: " + multiplicityRaw);
        };
    }
}
