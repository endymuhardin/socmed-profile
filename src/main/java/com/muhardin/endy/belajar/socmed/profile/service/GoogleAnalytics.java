package com.muhardin.endy.belajar.socmed.profile.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.*;
import com.muhardin.endy.belajar.socmed.profile.dto.GaReportRow;
import com.muhardin.endy.belajar.socmed.profile.dto.GoogleAnalyticsReport;
import com.muhardin.endy.belajar.socmed.profile.entity.Website;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GoogleAnalytics {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleAnalytics.class);
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    
    @Value("${spring.application.name}") private String appname;
    @Value("${google.oauth.client-id}") private String clientId;
    @Value("${google.oauth.client-secret}") private String clientSecret;


    public List<Profile> getProfiles(Website website) {
        List<Profile> hasil = new ArrayList<>();

        try {
            Analytics analytics = getAnalytics(website);
            for (Account acc : analytics.management().accounts().list().execute().getItems()) {
                for (Webproperty prop : analytics.management().webproperties()
                        .list(acc.getId()).execute().getItems()) {
                    Profiles profiles = analytics.management().profiles().list(acc.getId(), prop.getId()).execute();
                    LOGGER.info("Jumlah Profile : "+profiles.getTotalResults());
                    for (Profile p : profiles.getItems()) {
                        LOGGER.info("Profile ID : {}",p.getId());
                        LOGGER.info("Profile Name : {}",p.getName());
                        LOGGER.info("Profile Account ID : {}",p.getAccountId());
                        LOGGER.info("Profile Web Property ID : {}",p.getWebPropertyId());
                        LOGGER.info("Profile Internal Web Property ID : {}",p.getInternalWebPropertyId());
                        hasil.add(p);
                    }
                }
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        return hasil;
    }

    public GoogleAnalyticsReport generateReport(Website website) {
        try {
            String metrics = "ga:visitors,ga:pageviews";
            String dimensions = "ga:city,ga:country";
            String sort = "ga:country,-ga:visitors,-ga:pageviews";

            Analytics analytics = getAnalytics(website);
            GaData hasil = analytics.data().ga()
                    .get("ga:" + website.getProfileId(), "30daysAgo", "today", metrics)
                    .setDimensions(dimensions)
                    .setSort(sort)
                    .execute();
            return printResults(hasil);
        } catch (Exception err) {
            err.printStackTrace();
            return GoogleAnalyticsReport.builder().build();
        }
    }

    private Analytics getAnalytics(Website website) throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(clientId, clientSecret)
                .build()
                .setAccessToken(website.getAccessToken())
                .setRefreshToken(website.getRefreshToken());

        return new Analytics.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(appname)
                .build();
    }

    private static GoogleAnalyticsReport printResults(GaData results) {
        Map<String, String> totalsMap = results.getTotalsForAllResults();

        for (Map.Entry entry : totalsMap.entrySet()) {
            LOGGER.info("{} : {}", entry.getKey(), entry.getValue());
        }

        LOGGER.info("Column Headers:");

        for (GaData.ColumnHeaders header : results.getColumnHeaders()) {
            LOGGER.info("Column Name: {}", header.getName());
            LOGGER.info("Column Type: {}", header.getColumnType());
            LOGGER.info("Column Data Type: {}", header.getDataType());
        }

        List<GaReportRow> rows = new ArrayList<>();

        // Print the rows of data.
        for (List<String> rowValues : results.getRows()) {
            GaReportRow row = GaReportRow.builder()
                    .city(rowValues.get(0))
                    .country(rowValues.get(1))
                    .visitors(Integer.valueOf(rowValues.get(2)))
                    .pageviews(Integer.valueOf(rowValues.get(3)))
                    .build();
            rows.add(row);
            StringBuilder rowData = new StringBuilder();
            for (String value : rowValues) {
                rowData.append(String.format("%-32s", value));
            }
            LOGGER.info(rowData.toString());
        }

        Integer totalPageview = Integer.valueOf(totalsMap.get("ga:pageviews"));
        Integer totalVisitor = Integer.valueOf(totalsMap.get("ga:visitors"));

        return GoogleAnalyticsReport.builder()
                .rows(rows)
                .totalPageviews(totalPageview)
                .totalVisitors(totalVisitor)
                .build();

    }

}
