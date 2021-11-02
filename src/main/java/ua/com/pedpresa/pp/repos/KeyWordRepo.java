package ua.com.pedpresa.pp.repos;

import ua.com.pedpresa.pp.domain.KeyWord;
import org.springframework.data.repository.CrudRepository;
import ua.com.pedpresa.pp.domain.PostMod;

import java.util.List;
import java.util.Optional;

public interface KeyWordRepo extends CrudRepository<KeyWord,Long> {
    List<KeyWord> findAllByOrderByIdDesc();
}
