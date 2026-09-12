package com.pharm.pharmavigil_platform.service.dashboard;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class DashboardQueryLoader {

    private static final Pattern SECTION_PATTERN = Pattern.compile("--\\s*\\[([^\\]]+)\\]");

    private Map<String, String> queries;

    @PostConstruct
    public void load() {
        try {
            Map<String, String> localMap = new LinkedHashMap<>();
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:queries/dashboard/*.jpql");
            for (Resource resource : resources) {
                String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                parseFile(content, localMap);
            }
            queries = Collections.unmodifiableMap(localMap);
            log.info("Loaded {} dashboard queries from {} files", queries.size(), resources.length);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load dashboard query files", e);
        }
    }

    private void parseFile(String content, Map<String, String> target) {
        Matcher matcher = SECTION_PATTERN.matcher(content);

        List<int[]> positions = new ArrayList<>();
        List<String> names = new ArrayList<>();

        while (matcher.find()) {
            positions.add(new int[]{matcher.start(), matcher.end()});
            names.add(matcher.group(1).trim());
        }

        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            if (target.containsKey(name)) {
                throw new IllegalStateException("Duplicate dashboard query key: " + name);
            }
            int bodyStart = positions.get(i)[1];
            int bodyEnd = i + 1 < positions.size() ? positions.get(i + 1)[0] : content.length();
            target.put(name, content.substring(bodyStart, bodyEnd).trim());
        }
    }

    public String get(String name) {
        String query = queries.get(name);
        if (query == null) {
            throw new IllegalStateException("Dashboard query not found: " + name);
        }
        return query;
    }
}
