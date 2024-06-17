package com.myblog.controller.admin;

import com.myblog.entity.Blog;
import com.myblog.entity.Manager;
import com.myblog.entity.Notice;
import com.myblog.service.admin.BlogsService;
import com.myblog.service.admin.ManagerService;
import com.myblog.service.admin.NoticesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
public class admin_loginController {

    private final ManagerService managerService;

    private final NoticesService noticesService;
//
    private final BlogsService blogsService;



    public admin_loginController(ManagerService managerService,
                           NoticesService noticesService,
                           BlogsService blogsService) {
        this.managerService = managerService;
        this.noticesService = noticesService;
        this.blogsService = blogsService;
    }

    @RequestMapping("/notices/{id}/update")
    public String updateNotices(@PathVariable("id") int id) {

        if(id == 1){
            Blog blog = blogsService.getMostLikesBlog();

            noticesService.deleteNoticeById(id);

            String name = blog.getUser().getUsername();
            String title = "优质内容";
            String content = "恭喜！ 博客 ";
            content += blog.getId() + " ";
            content += "成为今日优质内容推荐！";
            noticesService.insertNotice(id, title, content);
        }else if(id == 2){
            noticesService.deleteNoticeById(id);

            String title = "系统维护";
            String content = "注意！本系统将于 ";
            LocalDate current = LocalDate.now();
            LocalDate threeDaysLater = current.plusDays(3);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            content += threeDaysLater.format(formatter);
            content += " 进行维护！";
            noticesService.insertNotice(id, title, content);
        }

        return "redirect:/admin/index";
    }



    @GetMapping("/admin/login")
    public String showLoginForm(Model model) {
        model.addAttribute("manager", new Manager());
        List<Notice> notices = noticesService.getAllNotices();
        model.addAttribute("notices", notices);
        return "/admin/login";
    }

    @GetMapping("/admin/index")
    public String showIndexForm(Model model) {
        List<Notice> notices = noticesService.getAllNotices();
        model.addAttribute("notices", notices);

        return "admin/index";
    }


    @PostMapping("/admin/login")
    public String processLogin(@ModelAttribute("manager") Manager manager, HttpServletRequest request, BindingResult bindingResult) {
        String username = manager.getUsername();
        String password = manager.getPassword();
        Optional<Manager> found_manager = managerService.findByUsername(manager.getUsername());
        if (found_manager.isPresent() && found_manager.get().getPassword().equals(manager.getPassword())) {
            request.getSession().setAttribute("currentUser", found_manager.get());
            return "redirect:/admin/index";
        } else {
            bindingResult.rejectValue("password", "error.invalidPassword", "Invalid password.");
            return "admin/login";
        }
    }

}