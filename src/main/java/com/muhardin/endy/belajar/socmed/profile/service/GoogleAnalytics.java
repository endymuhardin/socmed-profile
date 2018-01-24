package com.muhardin.endy.belajar.socmed.profile.service;

import com.muhardin.endy.belajar.socmed.profile.dto.GoogleAnalyticsReport;
import org.springframework.stereotype.Service;

@Service
public class GoogleAnalytics {
    public GoogleAnalyticsReport generateReport(String userId) {
        return GoogleAnalyticsReport.builder().build();
    }
}
