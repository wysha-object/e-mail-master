package cn.com.wysha.e_mail_master.controller;

import cn.com.wysha.e_mail_master.model.dto.BasicAuthMailbox;
import cn.com.wysha.e_mail_master.service.MailService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping(path = "/add")
public class AddController {
    private final MailService mailService;

    @Autowired
    public AddController(MailService mailService) {
        this.mailService = mailService;
    }

    @ModelAttribute("mailbox")
    public BasicAuthMailbox model(){
        return new BasicAuthMailbox();
    }
    @GetMapping
    public String get(){
        return "add";
    }
    @PostMapping
    public String post(@Valid @ModelAttribute("mailbox") BasicAuthMailbox mailBox, Errors errors){
        if (errors.hasErrors()){
            return "add";
        }
        try {
            mailService.addMailBox(mailBox);
        } catch (Exception e) {
            log.error(e.toString());
            return "add";
        }
        return "redirect:/";
    }

}
