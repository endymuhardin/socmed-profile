package com.muhardin.endy.belajar.socmed.profile.controller;

import com.muhardin.endy.belajar.socmed.profile.dao.WebsiteDao;
import com.muhardin.endy.belajar.socmed.profile.entity.Website;
import com.muhardin.endy.belajar.socmed.profile.service.GoogleAnalytics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@SessionAttributes("user")
public class SocmedProfileController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SocmedProfileController.class);

    @Autowired private GoogleAnalytics googleAnalytics;
    @Autowired private WebsiteDao websiteDao;

    @GetMapping("/")
    public String indexPage() {
        return "redirect:/website/form";
    }

    @GetMapping("/website/form")
    public void displayWebsiteForm() { }

    @PostMapping("/website/form")
    public String processWebsiteForm(@RequestParam String user, Model model) {
        model.addAttribute("user", user); // simpan di session
        Website website = websiteDao.findByUser(user);
        if (website == null) {
            website = new Website();
            website.setUser(user);
            websiteDao.save(website);
            return "redirect:/google/auth/start";
        }

        if (website.getAccessToken() == null) {
            return "redirect:/google/auth/start";
        }

        return "redirect:analytics";
    }

    @GetMapping("/website/select")
    public void displayFormSelectView(Model model, @SessionAttribute String user) {
        Website website = websiteDao.findByUser(user);
        model.addAttribute("daftarProfil", googleAnalytics.getProfiles(website));
    }

    @PostMapping("/website/select")
    public String processFormSelectView(@RequestParam String profile, @SessionAttribute String user) {
        Website website = websiteDao.findByUser(user);
        website.setProfileId(profile);
        websiteDao.save(website);
        return "redirect:analytics";
    }

    @GetMapping("/website/analytics")
    public String displayWebsiteAnalytics(Model model, @SessionAttribute(required = false) String user) {
        if (user == null) {
            LOGGER.error("User null");
            return "redirect:/website/form";
        }
        Website website = websiteDao.findByUser(user);

        if (website == null) {
            return "redirect:/website/form";
        }

        if (website.getAccessToken() == null) {
            return "redirect:/google/auth/start";
        }

        model.addAttribute("gaReport", googleAnalytics.generateReport(website));
        return null;
    }

}
