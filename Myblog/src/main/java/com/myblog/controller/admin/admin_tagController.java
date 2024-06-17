package com.myblog.controller.admin;

//import com.myblog.entity.Articles;
import com.myblog.entity.Tag;
import com.myblog.entity.User;
//import com.myblog.service.admin.ArticlesService;
//import com.myblog.service.admin.LabelsService;
import com.myblog.service.admin.TagsService;
import com.myblog.service.admin.UserManageService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import com.myblog.entity.Manager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Controller
public class admin_tagController {

    @Autowired
    private final TagsService tagsService;

//    @Autowired
//    private final ArticlesService articlesService;

//    public userManageController(UserManageService userManageService,
//                                ArticlesService articlesService) {
//        this.userManageService = userManageService;
//        this.articlesService = articlesService;
//
//    }

    public admin_tagController(TagsService tagsService){
        this.tagsService = tagsService;
    }

    @GetMapping("/tagsManage")
    public String getAllTags(Model model) {

        List<Tag> tags = tagsService.getAllTags();

        model.addAttribute("tags", tags);

        return "admin/tags";
    }

    @PostMapping("/admin/tags/add")
    public String addTag(@RequestParam("id") int id, @RequestParam("name") String name) {

        // 在这里实现添加标签的逻辑
        tagsService.insertTags(id, name);

        return "redirect:/tagsManage";
    }

    @PostMapping("/admin/tags/{id}/update")
    public String updateTag(@PathVariable("id") int id, @RequestParam("id") int newId, @RequestParam("name") String newName) {

        tagsService.deleteTagById(id);

        tagsService.insertTags(newId, newName);

        return "redirect:/tagsManage";
    }

    @RequestMapping("/admin/tags/{id}/delete")
    public String deleteTags(@PathVariable("id") int id) {

        // 根据id删除Tag对象
        tagsService.deleteTagById(id);

        return "redirect:/tagsManage";
    }

}
