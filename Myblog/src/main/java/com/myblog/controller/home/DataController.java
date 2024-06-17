package com.myblog.controller.home;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.myblog.entity.Blog;
import com.myblog.entity.User;
import com.myblog.service.BlogService;
import com.myblog.service.DataAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DataController {

    @Autowired
    private DataAnalysisService dataAnalysisService;

    @Autowired
    private BlogService blogService;

    @RequestMapping("/data")
    public String data(Model model, HttpSession session){

        User user = (User) session.getAttribute("user");

        if(user == null) return "user/login";

        String username = user.getUsername();

        ArrayNode dayLogin = dataAnalysisService.getLoginCount("day");
        model.addAttribute("dayLogin",dayLogin);

        ArrayNode monthLogin = dataAnalysisService.getLoginCount("month");
        model.addAttribute("monthLogin",monthLogin);

        ArrayNode hourLogin = dataAnalysisService.getLoginCount("hour");
        model.addAttribute("hourLogin",hourLogin);

        ArrayNode dataKeyword = dataAnalysisService.getCountOfSearch();
        model.addAttribute("dataKeyword",dataKeyword);

        ArrayNode urlCost = dataAnalysisService.getRequestDurationStatistics();
        model.addAttribute("urlCost",urlCost);

        ArrayNode userActivity = dataAnalysisService.getUserActivityStatistics(username);
        model.addAttribute("userActivity",userActivity);

        ArrayNode articleRead = dataAnalysisService.getUserArticleViews(username);
        model.addAttribute("articleRead",articleRead);

        return "user/dataForm";
    }

    //获取推荐文章列表
    public List<Blog> recommendList(String username,Integer num){
        List<Integer> idList = dataAnalysisService.getRecommandList(username);

        List<Blog> blogList = new ArrayList<>();
        for(Integer id : idList ){
            blogList.add(blogService.getBlogById(id,true));
            num--;
            if(num <= 0) break;
        }

        return blogList;
    }

    @GetMapping("/data/searchUserActivity")
    public ResponseEntity<?> dataSearch(@RequestParam("username") String username){
        return ResponseEntity.ok(dataAnalysisService.getUserActivityStatistics(username));
    }

    @GetMapping("/data/searchKeywordCount")
    public ResponseEntity<?> dataSearchKeyword(@RequestParam("username") String username){
        return ResponseEntity.ok(dataAnalysisService.getUserArticleViews(username));
    }
}
