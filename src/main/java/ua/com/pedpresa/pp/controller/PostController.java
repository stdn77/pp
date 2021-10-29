package ua.com.pedpresa.pp.controller;

import ua.com.pedpresa.pp.domain.KeyWord;
import ua.com.pedpresa.pp.domain.PostMod;
import ua.com.pedpresa.pp.repos.KeyWordRepo;
import ua.com.pedpresa.pp.repos.PostModRepo;
import ua.com.pedpresa.pp.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class PostController {

    private final PostModRepo postModRepo;
    @Autowired
    private WordService wordService;

    public PostController(PostModRepo postModRepo) {
        this.postModRepo = postModRepo;
    }


    @GetMapping("/news")
    public String newsList(Model model){
        Iterable<PostMod> postMods = postModRepo.findAllByOrderByIdDesc();
        model.addAttribute("posts",postMods);
    return "news";
    }

    @GetMapping("/edit/{id}")
    public String newsEdit(@PathVariable (value = "id") long id, Model model){
        if(!postModRepo.existsById(id)) return "news";
        Optional<PostMod> editPost = postModRepo.findById(id);
        List<PostMod> res = new ArrayList<>();
        editPost.ifPresent(res::add);
        return getKey(model, res);
    }

    @PostMapping("/edit/{id}")
    public String newsChange(
            @PathVariable (value = "id") long id,
            @RequestParam(required = false) String text_old,
            @RequestParam(required = false) String text_new,
            Model model) {
        wordService.addSinonim(text_old,text_new);
        Optional<PostMod> editPost = postModRepo.findById(id);
        List<PostMod> res = new ArrayList<>();
        editPost.ifPresent(res::add);
        if(res.size() == 1) {
            PostMod pm = res.get(0);
            pm.setTitle_mod(pm.getTitle_mod().replaceAll(text_old,text_new));
            pm.setText_mod(pm.getText_mod().replaceAll(text_old,text_new));
            pm.setText_mod_tag(pm.getText_mod_tag().replaceAll(text_old,text_new));
            postModRepo.save(pm);
            res.set(0,pm);
        }
        return getKey(model, res);
    }

    private String getKey(Model model, List<PostMod> res) {
        wordService.getKeywords(res.get(0).getTitle_mod(),res.get(0).getText_mod());
        model.addAttribute("freqList",wordService.getWordsList(res.get(0).getTitle_mod(),res.get(0).getText_mod()));
        model.addAttribute("count",postModRepo.count());
        model.addAttribute("post",res);
        return "edit";
    }

    @GetMapping("/edit/{id}/delete")
    public String newsDelete(@PathVariable (value = "id") long id, Model model) {
        if (!postModRepo.existsById(id)) return "news";
        postModRepo.deleteById(id);
        wordService.updateNewsSeq();
        return newsList(model);
    }
}
