package com.codeoftheweb.salvo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface  GamePlayerRepository extends JpaRepository<GamePlayer, Long>{
    List<GamePlayer> findByPlayer (String userName);
    GamePlayer findById (long id);
}


