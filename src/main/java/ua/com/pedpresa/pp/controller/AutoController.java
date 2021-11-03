package ua.com.pedpresa.pp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.com.pedpresa.pp.domain.*;
import ua.com.pedpresa.pp.repos.*;
import ua.com.pedpresa.pp.service.WordService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class AutoController {

    @Autowired
    private Words1Repo words1Repo;
    @Autowired
    private Words2Repo words2Repo;
    @Autowired
    private Words3Repo words3Repo;
    @Autowired
    private Words4Repo words4Repo;
    @Autowired
    private Words5Repo words5Repo;
    @Autowired
    private WordService wordService;

    @GetMapping("/auto/{tab}")
    public String autoReplacer(@PathVariable Integer tab, Model model) {

        if (tab < 1 || tab > 5) tab = 5;
        switch (tab) {
            case 5: {
                Iterable<Words5> words5s = words5Repo.findAll();
                model.addAttribute("replaceWords", words5s);
                break;
            }
            case 4: {
                Iterable<Words4> words4s = words4Repo.findAll();
                model.addAttribute("replaceWords", words4s);
                break;
            }
            case 3: {
                Iterable<Words3> words3s = words3Repo.findAll();
                model.addAttribute("replaceWords", words3s);
                break;
            }
            case 2: {
                Iterable<Words2> words2s = words2Repo.findAll();
                model.addAttribute("replaceWords", words2s);
                break;
            }
            case 1: {
                Iterable<Words1> words1s = words1Repo.findAll();
                model.addAttribute("replaceWords", words1s);
                break;
            }
        }
        model.addAttribute("tab", tab);
        return "auto";
    }

    @PostMapping("/auto/{tab}/update/{id}")
    public String autoUpdate(
            @PathVariable Integer tab,
            @PathVariable Long id,
            @RequestParam String w,
            @RequestParam String s,
            Model model) {

        if (tab < 1 || tab > 5) tab = 5;
        switch (tab) {
            case 5: {
                Words5 words5 = new Words5(id, w, s);
                words5Repo.save(words5);
                break;
            }
            case 4: {
                Words4 words4 = new Words4(id, w, s);
                words4Repo.save(words4);
                break;
            }
            case 3: {
                Words3 words3 = new Words3(id, w, s);
                words3Repo.save(words3);
                break;
            }
            case 2: {
                Words2 words2 = new Words2(id, w, s);
                words2Repo.save(words2);
                break;
            }
            case 1: {
                Words1 words1 = new Words1(id, w, s);
                words1Repo.save(words1);
                break;
            }
        }
        return autoReplacer(tab, model);
    }

    @GetMapping("/auto/{tab}/delete/{id}")
    public String autoDelete(@PathVariable Integer tab,
                             @PathVariable Long id,
                             Model model) {
        if (tab < 1 || tab > 5) tab = 5;
        switch (tab) {
            case 5: words5Repo.deleteById(id);
            case 4: words4Repo.deleteById(id);
            case 3: words3Repo.deleteById(id);
            case 2: words2Repo.deleteById(id);
            case 1: words1Repo.deleteById(id);
        }
        return autoReplacer(tab, model);
    }
}