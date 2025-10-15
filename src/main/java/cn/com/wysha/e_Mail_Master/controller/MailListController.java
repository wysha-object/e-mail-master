package cn.com.wysha.e_mail_master.controller;

import cn.com.wysha.e_mail_master.model.intf.Mailbox;
import cn.com.wysha.e_mail_master.model.vo.MailView;
import cn.com.wysha.e_mail_master.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping(path = "/mail-list")
public class MailListController {
    private final MailService mailService;

    @Autowired
    public MailListController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping
    public String get(
            Model model,
            String address,
            String folder
    ) {
        //依据address属性获得对应的邮箱
        Optional<Mailbox> mailboxOptional = mailService.getAllMailbox().stream().filter(o -> Objects.equals(o.getAddress(), address)).findFirst();
        if (mailboxOptional.isEmpty()) return "redirect:/";
        Mailbox mailbox = mailboxOptional.get();

        //添加电子邮件进视图模型
        List<MailView> mailViews = mailService.getAllMailByOf(mailbox, folder).stream().map(MailView::new).sorted().toList().reversed();
        model.addAttribute("allMailView", mailViews);

        return "mail-list";
    }
}
