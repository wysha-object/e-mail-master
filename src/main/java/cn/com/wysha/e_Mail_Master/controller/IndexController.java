package cn.com.wysha.e_mail_master.controller;

import cn.com.wysha.e_mail_master.model.intf.Mailbox;
import cn.com.wysha.e_mail_master.model.vo.MailboxView;
import cn.com.wysha.e_mail_master.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping(path = "/")
public class IndexController {
    private final MailService mailService;

    @Autowired
    public IndexController(MailService mailService) {
        this.mailService = mailService;
    }

    @ModelAttribute
    public Model model(Model model) {
        Collection<Mailbox> allMailbox = mailService.getAllMailbox();
        return model
                .addAttribute("allMailboxView", allMailbox.stream().map(MailboxView::new).sorted().toList());
    }

    @GetMapping
    public String get() {
        return "index";
    }

    private Mailbox toMailbox(String address) {
        //依据address属性获得对应的邮箱
        Optional<Mailbox> mailboxOptional = mailService.getAllMailbox().stream().filter(o -> Objects.equals(o.getAddress(), address)).findFirst();
        return mailboxOptional.orElse(null);
    }

    @PostMapping
    public String post(
            String address
    ) {
        Mailbox mailbox = toMailbox(address);
        if (mailbox == null) return "redirect:/";

        //清空errors
        mailService.clearErrors(mailbox);

        return "redirect:/";
    }

    @PostMapping("/delete")
    public String delete(
            String address
    ) {
        Mailbox mailbox = toMailbox(address);
        if (mailbox == null) return "redirect:/";

        //删除邮箱
        mailService.removeMailBox(mailbox);

        return "redirect:/";
    }

    @PostMapping("/initial")
    public String initial(
            String address
    ) {
        Mailbox mailbox = toMailbox(address);
        if (mailbox == null) return "redirect:/";

        //重新初始化邮箱
        mailService.reinitialMailBox(mailbox);

        return "redirect:/";
    }

    @PostMapping("/sync")
    public String sync(
            String address
    ) {
        Mailbox mailbox = toMailbox(address);
        if (mailbox == null) return "redirect:/";

        //重新同步邮箱
        mailService.resyncMailBox(mailbox);

        return "redirect:/";
    }
}
