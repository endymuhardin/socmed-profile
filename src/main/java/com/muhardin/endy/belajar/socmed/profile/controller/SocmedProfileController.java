package com.muhardin.endy.belajar.socmed.profile.controller;

import com.muhardin.endy.belajar.socmed.profile.dao.WebsiteDao;
import com.muhardin.endy.belajar.socmed.profile.entity.Website;
import com.muhardin.endy.belajar.socmed.profile.service.GoogleAnalytics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SocmedProfileController {
    @Autowired private GoogleAnalytics googleAnalytics;
    @Autowired private WebsiteDao websiteDao;

    @GetMapping("/website/form")
    public void displayWebsiteForm() { }

    @PostMapping("/website/form")
    public String processWebsiteForm(@RequestParam String url,
                                     RedirectAttributes redirectAttrs) {
        return "redirect:analytics";
    }

    @GetMapping("/website/analytics")
    public String displayWebsiteAnalytics(Model model) {
        Website website = websiteDao.findByUrl("https://software.endy.muhardin.com");

        if (website == null || website.getAccessToken() == null) {
            return "redirect:/google/auth/start";
        }

        model.addAttribute("data", googleAnalytics.generateReport(website));

        return "/website/analytics";
    }

}
