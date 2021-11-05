package ua.com.pedpresa.pp.controller;

import org.springframework.web.bind.annotation.*;
import ua.com.pedpresa.pp.domain.KeyWord;
import ua.com.pedpresa.pp.domain.PostMod;
import ua.com.pedpresa.pp.repos.KeyWordRepo;
import ua.com.pedpresa.pp.repos.PostModRepo;
import ua.com.pedpresa.pp.service.PushService;
import ua.com.pedpresa.pp.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class PostController {

    private final PostModRepo postModRepo;
    @Autowired
    private WordService wordService;
    @Autowired
    private PushService pushService;

    public PostController(PostModRepo postModRepo) {
        this.postModRepo = postModRepo;
    }


    @GetMapping("/news")
    public String newsList(Model model) {
        Iterable<PostMod> postMods = postModRepo.findAllByOrderByIdDesc();
        model.addAttribute("posts", postMods);
        return "news";
    }

    @GetMapping("/edit/{id}")
    public String newsEdit(@PathVariable(value = "id") long id, Model model) {
        if (!postModRepo.existsById(id)) return "news";
        Optional<PostMod> editPost = postModRepo.findById(id);
        List<PostMod> res = new ArrayList<>();
        editPost.ifPresent(res::add);
        String title = res.get(0).getTitle_mod();
        String text = res.get(0).getText_mod();
        List<String> kws = wordService.getKeywords(title, text);
        kws.forEach(System.out::println);
//        if(kws.size()>1 && res.get(0).getMain_keyword()!=null) {
//            if(kws.contains(res.get(0).getMain_keyword())) System.out.println(res.get(0).getMain_keyword()+" 1111");
//            kws.add(res.get(0).getMain_keyword());
//        }
        model.addAttribute("keywords", kws);
        model.addAttribute("freqList", wordService.getWordsList(title, text));
        model.addAttribute("count", postModRepo.count());
        model.addAttribute("post", res);
        return "edit";
    }

    @PostMapping("/edit/{id}")
    public String newsChange(
            @PathVariable(value = "id") long id,
            @RequestParam(required = false) String text_old,
            @RequestParam(required = false) String text_new,
            Model model) {

//        System.out.println("*************************** HERE");
        wordService.addSinonim(text_old, text_new);
        Optional<PostMod> editPost = postModRepo.findById(id);
        List<PostMod> res = new ArrayList<>();
        editPost.ifPresent(res::add);
        if (res.size() == 1) {
            PostMod pm = res.get(0);
            pm.setTitle_mod(pm.getTitle_mod().replaceAll(text_old, text_new));
            pm.setText_mod(pm.getText_mod().replaceAll(text_old, text_new));
            pm.setText_mod_tag(pm.getText_mod_tag().replaceAll(text_old, text_new));
            postModRepo.save(pm);
            res.set(0, pm);
        }
        return newsEdit(id, model);
    }

    @PostMapping("/edit/{id}/key/")
    public String choiceKeyWord(
            @PathVariable(value = "id") long id,
            @RequestParam(required = false) String kw,
            Model model) {

        Optional<PostMod> editPost = postModRepo.findById(id);
        List<PostMod> res = new ArrayList<>();
        editPost.ifPresent(res::add);
        if (res.size() == 1) {
            PostMod pm = res.get(0);
            pm.setMain_keyword(kw);
            String pictAndId = wordService.getPictString(kw);
            System.out.println(pictAndId);
            if (pictAndId != null && !pictAndId.isEmpty()) {
                pm.setPict(pictAndId.substring(0, pictAndId.indexOf("*")));
                pm.setId_pict(Long.parseLong(pictAndId.substring(pictAndId.lastIndexOf("*") + 1)));
            }
            postModRepo.save(pm);
        }
        return "redirect:/edit/{id}";
    }


    @GetMapping("/edit/{id}/delete")
    public String newsDelete(@PathVariable(value = "id") long id, Model model) {
        if (!postModRepo.existsById(id)) return "news";
        postModRepo.deleteById(id);
        wordService.updateNewsSeq();
        return newsList(model);
    }

    @GetMapping("/edit/{id}/push")
    public String newsPush(@PathVariable(value = "id") long id, Model model) {
        if (!postModRepo.existsById(id)) return "news";
        Optional<PostMod> editPost = postModRepo.findById(id);
        List<PostMod> res = new ArrayList<>();
        editPost.ifPresent(res::add);
        if (res.size() == 1) {
            PostMod pm = res.get(0);
            pushService.pushPost(pm);
            pm.setIsEx(1);
            postModRepo.save(pm);
        }
        return "redirect:/news";
    }
}
