package cn.com.wysha.e_mail_master.controller;

import cn.com.wysha.e_mail_master.model.vo.MailView;
import cn.com.wysha.e_mail_master.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping(path = "/mail-text")
public class MailTextController {
    private final MailService mailService;

    @Autowired
    public MailTextController(MailService mailService) {
        this.mailService = mailService;
    }

    @ResponseBody
    @GetMapping
    public String get(String id) {
        return new MailView(mailService.getMailById(id)).getText();
    }
}
