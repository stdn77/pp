package ua.com.pedpresa.pp.domain;

import javax.persistence.*;

@Entity
@Table(name = "words_3")
public class Words3 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String word;
    private String sinonim;

    public Words3() {
    }

    public Words3(String word, String sinonim) {
        this.word = word;
        this.sinonim = sinonim;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getSinonim() {
        return sinonim;
    }

    public void setSinonim(String sinonim) {
        this.sinonim = sinonim;
    }
}
