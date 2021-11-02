package ua.com.pedpresa.pp.repos;

import ua.com.pedpresa.pp.domain.KeyWord;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface KeyWordRepo extends CrudRepository<KeyWord,Long> {
}
