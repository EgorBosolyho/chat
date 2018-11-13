package com.example.controller;

import com.example.entity.Message;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.repos.MessageRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Controller
public class MainController {
    @Autowired
    private MessageRepos messageRepos;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/")
    public String greeting(Map<String, Object> model) {
        return "greeting";
    }

    @GetMapping("main")
    public String main(
            @RequestParam(required = false, defaultValue = "") String filter,
            Model model){
        Iterable<Message> messages;

        if(filter != null && !filter.isEmpty()) {
            messages = messageRepos.findByTag(filter);

        } else{
            messages = messageRepos.findAll();
        }

        model.addAttribute("messages", messages);
        model.addAttribute("filter", filter);
        return "main";
    }

    @PostMapping("main")
    public String add(
            @AuthenticationPrincipal User user,
            @RequestParam String text,
            @RequestParam String tag,
            @RequestParam("file") MultipartFile file,
            Model model) throws IOException {

        Message message = new Message(text, tag, user);

        if(file != null && !file.getOriginalFilename().isEmpty()){
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }

            String uuidName = UUID.randomUUID().toString();
            String endFileName = uuidName + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + endFileName));

            message.setFilename(endFileName);
        }

        messageRepos.save(message);

        Iterable<Message> messages = messageRepos.findAll();
        model.addAttribute("messages", messages);

        return "main";
    }
}