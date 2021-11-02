package ua.com.pedpresa.pp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.com.pedpresa.pp.domain.KeyWord;
import ua.com.pedpresa.pp.repos.KeyWordRepo;

@Controller
public class KeywordController {
    @Autowired
    private KeyWordRepo keyWordRepo;

    @GetMapping("/keywords")
    public String keyWordsEdit(Model model){
        Iterable<KeyWord> keyWords = keyWordRepo.findAll();
        model.addAttribute("keywords",keyWords);
        return "keywords";
    }
    @PostMapping("/keywords")
    public String keyWordsEdit(
            @RequestParam(required = true, defaultValue = "") String new_key,
            @RequestParam(required = false) String new_tag,
            @RequestParam(required = false) String new_pict,
            Model model){
        KeyWord keyWord = new KeyWord(new_key, new_tag, new_pict,0L);
        keyWordRepo.save(keyWord);
        Iterable<KeyWord> keyWords = keyWordRepo.findAll();
        model.addAttribute("keywords",keyWords);
        return "keywords";
    }
}
