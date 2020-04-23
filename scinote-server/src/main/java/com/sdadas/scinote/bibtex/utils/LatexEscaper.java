package com.sdadas.scinote.bibtex.utils;

import com.google.common.io.CharStreams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Sławomir Dadas
 */
public class LatexEscaper implements Serializable, Function<String, String> {

    private final Map<Integer, String> mapping;

    public LatexEscaper() {
        this.mapping = createMapping();
    }

    @Override
    public String apply(String input) {
        if(StringUtils.isBlank(input)) return input;
        StringBuilder builder = new StringBuilder();
        int length = input.length();
        for (int offset = 0; offset < length;) {
            int codepoint = input.codePointAt(offset);
            String escape = mapping.get(codepoint);
            if(escape != null) {
                builder.append(escape);
            } else {
                char[] chars = Character.toChars(codepoint);
                builder.append(chars, 0, chars.length);
            }
            offset += Character.charCount(codepoint);
        }
        return builder.toString();
    }

    @SuppressWarnings("all")
    private Map<Integer, String> createMapping() {
        Resource resource = new ClassPathResource("/lists/bibtex_map.txt");
        Map<Integer, String> res = new HashMap<>();
        try(InputStream is = resource.getInputStream()) {
            List<String> lines = CharStreams.readLines(new InputStreamReader(is, StandardCharsets.UTF_8));
            lines.forEach(line -> {
                String[] values = StringUtils.split(line);
                int codepoint = Integer.parseInt(values[0].substring(2), 16);
                String escape = "{" + values[1] + "}";
                res.put(codepoint, escape);
            });
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return res;
    }
}
