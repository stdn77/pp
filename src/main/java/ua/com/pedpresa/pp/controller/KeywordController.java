package ua.com.pedpresa.pp.controller;

import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.com.pedpresa.pp.domain.KeyWord;
import ua.com.pedpresa.pp.domain.PostMod;
import ua.com.pedpresa.pp.repos.KeyWordRepo;
import ua.com.pedpresa.pp.service.WordService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class KeywordController {
    @Autowired
    private KeyWordRepo keyWordRepo;
    @Autowired
    private WordService wordService;

    @GetMapping("/keywords")
    public String keyWordsEdit(Model model){
        Iterable<KeyWord> keyWords = keyWordRepo.findAllByOrderByIdDesc();
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
        Iterable<KeyWord> keyWords = keyWordRepo.findAllByOrderByIdDesc();
        model.addAttribute("keywords",keyWords);
        return "keywords";
    }

    @GetMapping("/keyedit/{id}")
    public String keyEdit(@PathVariable(name = "id") Long id,  Model model){
        Optional<KeyWord> keyWords = keyWordRepo.findById(id);
        List<KeyWord> keyList = new ArrayList<>();
        keyWords.ifPresent(keyList::add);
        if(!keyList.isEmpty()){
            model.addAttribute("key",keyList.get(0));
            model.addAttribute("count",keyWordRepo.count());
        }
        return "keyedit";
    }

    @PostMapping("/keyedit/{id}/save")
    public String keySave(
            @PathVariable Long id,
            @RequestParam String keyword,
            @RequestParam String tag,
            @RequestParam String pict,
            @RequestParam Long id_pict,
            Model model){
        Optional<KeyWord> editKey = keyWordRepo.findById(id);
        List<KeyWord> res = new ArrayList<>();
        editKey.ifPresent(res::add);
        if(res.size() == 1) {
            KeyWord keyWord = res.get(0);
            keyWord.setKey_words(keyword);
            keyWord.setTag(tag);
            keyWord.setPict(pict);
            keyWord.setId_pict(id_pict);
            keyWordRepo.save(keyWord);
            res.set(0, keyWord);
        }
        Iterable<KeyWord> keyWords = keyWordRepo.findAllByOrderByIdDesc();
        model.addAttribute("keywords",keyWords);
        return "redirect:/keywords";
    }

    @GetMapping("/keyedit/{id}/delete")
    public String keyDelete(@PathVariable Long id, Model model) {
    keyWordRepo.deleteById(id);

        Iterable<KeyWord> keyWords = keyWordRepo.findAllByOrderByIdDesc();
        model.addAttribute("keywords",keyWords);
        return "redirect:/keywords";
    }

    @GetMapping("/keywords/pict")
    public String updatePictId(Model model){
        System.out.println("+++++++++++++++++++ HERE");
        wordService.updatePictIdFromDB();
        return  "redirect:/keywords";
    }

}
