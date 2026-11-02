package com.foundit.apk.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Penyimpanan sementara. Kelas ini dapat diganti implementasi JDBC/MySQL kelak. */
public class ReportRepository {
    private static final ReportRepository INSTANCE = new ReportRepository();
    private final List<Report> reports = new ArrayList<>();
    private ReportRepository() {}
    public static ReportRepository getInstance() { return INSTANCE; }
    public void add(Report report) { reports.add(report); }
    public List<Report> findAll() { return Collections.unmodifiableList(reports); }
    public long count(String type) { return reports.stream().filter(r -> r.getType().equals(type)).count(); }
    public static class Report {
        private final String name, type, category, location, description, date, photo;
        public Report(String name, String type, String category, String location, String description, LocalDate date, String photo) {
            this.name=name; this.type=type; this.category=category; this.location=location;
            this.description=description; this.date=date.toString(); this.photo=photo;
        }
        public String getName(){return name;} public String getType(){return type;} public String getCategory(){return category;}
        public String getLocation(){return location;} public String getDescription(){return description;} public String getDate(){return date;} public String getPhoto(){return photo;}
    }
}
