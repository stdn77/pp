package ua.com.pedpresa.pp.domain;

import javax.persistence.*;


@Entity
@Table(name = "news_mod")
public class PostMod {
    @Id
    @Column(name = "id")
    private Long id;
    private Long canonical;
    private String title_origin;
    private String text_origin;
    private String title_mod;
    private String text_mod;
    private String text_mod_tag;
    private Integer isEx;


    public PostMod() {
    }

    public PostMod(Long id, Long canonical, String title_origin, String text_origin, String title_mod, String text_mod, String text_mod_tag, Integer isEx) {
        this.id = id;
        this.canonical = canonical;
        this.title_origin = title_origin;
        this.text_origin = text_origin;
        this.title_mod = title_mod;
        this.text_mod = text_mod;
        this.text_mod_tag = text_mod_tag;
        this.isEx = isEx;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCanonical() {
        return canonical;
    }

    public void setCanonical(Long canonical) {
        this.canonical = canonical;
    }

    public String getTitle_origin() {
        return title_origin;
    }

    public void setTitle_origin(String title_origin) {
        this.title_origin = title_origin;
    }

    public String getText_origin() {
        return text_origin;
    }

    public void setText_origin(String text_origin) {
        this.text_origin = text_origin;
    }

    public String getTitle_mod() {
        return title_mod;
    }

    public void setTitle_mod(String title_mod) {
        this.title_mod = title_mod;
    }

    public String getText_mod() {
        return text_mod;
    }

    public void setText_mod(String text_mod) {
        this.text_mod = text_mod;
    }

    public String getText_mod_tag() {
        return text_mod_tag;
    }

    public void setText_mod_tag(String text_mod_tag) {
        this.text_mod_tag = text_mod_tag;
    }

    public Integer getIsEx() {
        return isEx;
    }

    public void setIsEx(Integer isEx) {
        this.isEx = isEx;
    }
}
