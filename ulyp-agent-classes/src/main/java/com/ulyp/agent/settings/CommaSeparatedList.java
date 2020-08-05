package com.ulyp.agent.settings;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommaSeparatedList {

    public static List<String> parse(String text) {

        // TODO maybe validate a bit

        String[] split = text.split(",");
        if (split.length == 1 && StringUtils.isEmpty(split[0])) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(split);
        }
    }
}
