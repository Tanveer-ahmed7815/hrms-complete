package com.te.flinko.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.AttributeConverter;

import org.apache.commons.lang3.StringUtils;

public class MapOfListToStringConverter implements AttributeConverter<Map<String, List<String>>, String>{

	@Override
	public String convertToDatabaseColumn(Map<String, List<String>> attribute) {
		if (attribute == null)
			return "";
		return attribute.entrySet().stream().map(e -> e.getKey() + "=[" + StringUtils.join(e.getValue().toArray(), ',') + "]").collect(Collectors.joining(","));
	}

	@Override
	public Map<String, List<String>> convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.equals(""))
			return Collections.emptyMap();
		return Stream.of(dbData.split("],")).map(work -> work.replace("[", "").replace("]", "").split("="))
				.collect(Collectors.toMap(word -> word[0], word -> Arrays.asList(word[1].split(","))));
	}

}
