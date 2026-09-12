package com.pharm.pharmavigil_platform.service.dashboard;

import com.pharm.pharmavigil_platform.domain.SummaryGroupBy;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class DashboardLinksBuilder {

    public Map<String, String> machineLinks(UUID machineId, LocalDate start, LocalDate end,
                                             List<UUID> shiftIds, List<UUID> staffIds) {
        String suffix = "?machineId=" + machineId + "&" + rangeParams(start, end, shiftIds, staffIds, null);
        Map<String, String> links = new LinkedHashMap<>();
        links.put("products", "/api/supervisor/machine/products" + suffix);
        return links;
    }

    public Map<String, String> staffLinks(UUID staffId, LocalDate start, LocalDate end,
                                           List<UUID> shiftIds, List<UUID> machineIds) {
        String suffix = "?staffId=" + staffId + "&" + rangeParams(start, end, shiftIds, null, machineIds);
        Map<String, String> links = new LinkedHashMap<>();
        links.put("products", "/api/supervisor/staff/products" + suffix);
        return links;
    }

    public Map<String, String> shiftLinks(UUID shiftId, LocalDate start, LocalDate end,
                                           List<UUID> staffIds, List<UUID> machineIds) {
        String suffix = "?shiftId=" + shiftId + "&" + rangeParams(start, end, null, staffIds, machineIds);
        Map<String, String> links = new LinkedHashMap<>();
        links.put("products", "/api/supervisor/shift/products" + suffix);
        return links;
    }

    public Map<String, String> dateLinks(LocalDate date,
                                          List<UUID> shiftIds, List<UUID> staffIds, List<UUID> machineIds) {
        String qs = dateQs(shiftIds, staffIds, machineIds);
        String suffix = "?date=" + date + (qs.isEmpty() ? "" : "&" + qs.substring(1));
        Map<String, String> links = new LinkedHashMap<>();
        links.put("products", "/api/supervisor/date/products" + suffix);
        return links;
    }

    public Map<String, String> stopGroupLinks(UUID stopTypeId, String stopName, LocalDate start, LocalDate end,
                                               List<UUID> shiftIds, List<UUID> staffIds,
                                               List<UUID> machineIds) {
        StringBuilder sb = new StringBuilder("/api/supervisor/stop/machines?");
        sb.append(stopTypeId != null ? "stopTypeId=" + stopTypeId : "stopName=" + enc(stopName));
        sb.append("&startDate=").append(start).append("&endDate=").append(end);
        appendList(sb, "shiftIds", shiftIds);
        appendList(sb, "staffIds", staffIds);
        appendList(sb, "machineIds", machineIds);
        Map<String, String> links = new LinkedHashMap<>();
        links.put("machines", sb.toString());
        return links;
    }

    public Map<String, String> stopMachineLinks(UUID workReportMachineId, UUID stopTypeId, String stopName) {
        StringBuilder sb = new StringBuilder("/api/supervisor/stop/products?workReportMachineId=").append(workReportMachineId);
        sb.append("&").append(stopTypeId != null ? "stopTypeId=" + stopTypeId : "stopName=" + enc(stopName));
        Map<String, String> links = new LinkedHashMap<>();
        links.put("products", sb.toString());
        return links;
    }

    public String buildGroupedLink(SummaryGroupBy groupBy, LocalDate startDate, LocalDate endDate,
                                    List<UUID> shiftIds, List<UUID> staffIds, List<UUID> machineIds) {
        StringBuilder sb = new StringBuilder("/api/supervisor/grouped?groupBy=").append(groupBy.name());
        sb.append("&startDate=").append(startDate).append("&endDate=").append(endDate);
        appendList(sb, "shiftIds", shiftIds);
        appendList(sb, "staffIds", staffIds);
        appendList(sb, "machineIds", machineIds);
        return sb.toString();
    }

    private String rangeParams(LocalDate start, LocalDate end,
                                List<UUID> shiftIds, List<UUID> staffIds, List<UUID> machineIds) {
        StringBuilder sb = new StringBuilder("startDate=").append(start).append("&endDate=").append(end);
        appendList(sb, "shiftIds", shiftIds);
        appendList(sb, "staffIds", staffIds);
        appendList(sb, "machineIds", machineIds);
        return sb.toString();
    }

    private String dateQs(List<UUID> shiftIds, List<UUID> staffIds, List<UUID> machineIds) {
        StringBuilder sb = new StringBuilder();
        appendList(sb, "shiftIds", shiftIds);
        appendList(sb, "staffIds", staffIds);
        appendList(sb, "machineIds", machineIds);
        String params = sb.toString();
        if (params.isEmpty()) return "";
        return "?" + params.substring(1);
    }

    private void appendList(StringBuilder sb, String param, List<UUID> values) {
        if (values != null && !values.isEmpty()) {
            for (UUID v : values) {
                sb.append("&").append(param).append("=").append(v);
            }
        }
    }

    private String enc(String v) {
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }
}
