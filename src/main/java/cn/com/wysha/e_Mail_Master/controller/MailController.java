package cn.com.wysha.e_mail_master.controller;

import cn.com.wysha.e_mail_master.model.vo.MailView;
import cn.com.wysha.e_mail_master.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping(path = "/mail")
public class MailController {
    private final MailService mailService;

    @Autowired
    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping
    public String get(
            Model model,
            String id
    ) {
        model.addAttribute("mail", new MailView(mailService.getMailById(id)));
        return "mail";
    }
}
