package io.github.rafaelpino.quarkussocial.domain.repository;

import io.github.rafaelpino.quarkussocial.domain.model.Follower;
import io.github.rafaelpino.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import javax.security.auth.login.Configuration;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public boolean follows(User follower, User user){ //para saber se o seguidor já está seguindo
        Map<String, Object> params = new HashMap<>();
        params.put("follower", follower);
        params.put("user", user);

        //outra forma de fazer a baixo
       // Map<String,Object> params = Parameters.with("follower", follower).and("user", user).map();
        PanacheQuery<Follower> query = find("follower =:follower and user =:user", params); //dois pontinho para indicar que é um parametro

        //Follower result = query.firstResult(); essa não retorna o pode ser nulo então não é indicada
        Optional<Follower> result = query.firstResultOptional();

        return result.isPresent(); //se estiver presente o resultado significa que já segue
    }

    public List<Follower> findByUser(Long userId){
        PanacheQuery<Follower> query = find("user.id", userId);
        return query.list();
    }

    public Object deleteByFollowerAndUser(Long followerId, Long userId) {
        Map<String, Object> params = Parameters
                .with("userId", userId)
                .and("followerId", followerId).map();
        //esses são os parametro que usará na url da deleção
        return delete("follower.id =:followerId and user.id =: userId", params);
    }
}
