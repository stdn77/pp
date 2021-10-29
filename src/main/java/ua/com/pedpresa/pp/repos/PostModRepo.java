package ua.com.pedpresa.pp.repos;

import org.springframework.data.repository.CrudRepository;
import ua.com.pedpresa.pp.domain.PostMod;

import java.util.List;

public interface PostModRepo extends CrudRepository<PostMod,Long> {
    List<PostMod> findAllByOrderByIdDesc();

}
