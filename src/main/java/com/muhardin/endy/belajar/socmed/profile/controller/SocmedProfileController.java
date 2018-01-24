package com.muhardin.endy.belajar.socmed.profile.controller;

import com.muhardin.endy.belajar.socmed.profile.dto.GoogleAnalyticsReport;
import com.muhardin.endy.belajar.socmed.profile.service.GoogleAnalytics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SocmedProfileController {
    @Autowired private GoogleAnalytics googleAnalytics;

    @GetMapping("/website/form")
    public void displayWebsiteForm() { }

    @PostMapping("/website/form")
    public String processWebsiteForm(@RequestParam String userId,
                                     RedirectAttributes redirectAttrs) {
        GoogleAnalyticsReport report = googleAnalytics.generateReport(userId);
        redirectAttrs.addFlashAttribute("gaReport", report);
        return "redirect:analytics";
    }

    @GetMapping("/website/analytics")
    public void displayWebsiteAnalytics() { }

}
