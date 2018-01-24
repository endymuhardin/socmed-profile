package com.muhardin.endy.belajar.socmed.profile.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.*;
import com.muhardin.endy.belajar.socmed.profile.dto.GoogleAnalyticsReport;
import com.muhardin.endy.belajar.socmed.profile.entity.Website;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleAnalytics {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    @Value("${spring.application.name}") private String appname;

    public GoogleAnalyticsReport generateReport(Website website) {
        try {
            GoogleCredential credential = new GoogleCredential()
                    .setAccessToken(website.getAccessToken());

            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Analytics analytics = new Analytics.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(appname)
                    .build();
            System.out.println("Mengakses Google API");

            Accounts accounts = analytics.management().accounts().list().execute();

            System.out.println("Daftar Akun : ");
            for (Account acc : accounts.getItems()) {
                System.out.println("==============================");
                System.out.println("ID : "+acc.getId());
                System.out.println("Name : "+acc.getName());

                Webproperties properties = analytics.management().webproperties()
                        .list(acc.getId()).execute();

                for (Webproperty prop : properties.getItems()) {
                    Profiles profiles = analytics.management().profiles().list(acc.getId(), prop.getId()).execute();
                    for (Profile profile : profiles.getItems()) {
                        System.out.println("Profile Info");
                        System.out.println("Account ID: " + profile.getAccountId());
                        System.out.println("Web Property ID: " + profile.getWebPropertyId());
                        System.out.println("Internal Web Property ID: " + profile.getInternalWebPropertyId());
                        System.out.println("Profile ID: " + profile.getId());
                        System.out.println("Profile Name: " + profile.getName());

                        printResults(analytics.data().ga()
                                .get("ga:" + profile.getId(), "7daysAgo", "today", "ga:sessions")
                                .execute());
                    }
                }
            }

            return GoogleAnalyticsReport.builder().build();
        } catch (Exception err) {
            err.printStackTrace();
            return GoogleAnalyticsReport.builder().build();
        }
    }

    private static void printResults(GaData results) {
        // Parse the response from the Core Reporting API for
        // the profile name and number of sessions.
        if (results != null && !results.getRows().isEmpty()) {
            System.out.println("View (Profile) Name: "
                    + results.getProfileInfo().getProfileName());
            System.out.println("Total Sessions: " + results.getRows().get(0).get(0));
        } else {
            System.out.println("No results found");
        }
    }

}
