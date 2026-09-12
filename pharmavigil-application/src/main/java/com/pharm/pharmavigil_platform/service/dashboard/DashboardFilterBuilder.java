package com.pharm.pharmavigil_platform.service.dashboard;

import jakarta.persistence.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
public class DashboardFilterBuilder {

    public String rangeFilter(List<UUID> shiftIds, List<UUID> staffIds, List<UUID> machineIds) {
        StringBuilder w = new StringBuilder("WHERE wr.reportDate BETWEEN :startDate AND :endDate");
        if (notEmpty(shiftIds)) w.append(" AND sh.id IN :shiftIds");
        if (notEmpty(staffIds)) w.append(" AND u.id IN :staffIds");
        if (notEmpty(machineIds)) w.append(" AND mach.id IN :machineIds");
        return w.toString();
    }

    public String dateFilter(List<UUID> shiftIds, List<UUID> staffIds, List<UUID> machineIds) {
        StringBuilder w = new StringBuilder("WHERE wr.reportDate = :date");
        if (notEmpty(shiftIds)) w.append(" AND sh.id IN :shiftIds");
        if (notEmpty(staffIds)) w.append(" AND u.id IN :staffIds");
        if (notEmpty(machineIds)) w.append(" AND mach.id IN :machineIds");
        return w.toString();
    }

    public void bindRange(Query q, LocalDate startDate, LocalDate endDate,
                          List<UUID> shiftIds, List<UUID> staffIds, List<UUID> machineIds) {
        q.setParameter("startDate", startDate);
        q.setParameter("endDate", endDate);
        if (notEmpty(shiftIds)) q.setParameter("shiftIds", shiftIds);
        if (notEmpty(staffIds)) q.setParameter("staffIds", staffIds);
        if (notEmpty(machineIds)) q.setParameter("machineIds", machineIds);
    }

    public void bindDate(Query q, LocalDate date,
                         List<UUID> shiftIds, List<UUID> staffIds, List<UUID> machineIds) {
        q.setParameter("date", date);
        if (notEmpty(shiftIds)) q.setParameter("shiftIds", shiftIds);
        if (notEmpty(staffIds)) q.setParameter("staffIds", staffIds);
        if (notEmpty(machineIds)) q.setParameter("machineIds", machineIds);
    }

    public boolean notEmpty(List<?> list) {
        return list != null && !list.isEmpty();
    }

    public static long toLong(Object val) {
        return val == null ? 0L : ((Number) val).longValue();
    }
}
