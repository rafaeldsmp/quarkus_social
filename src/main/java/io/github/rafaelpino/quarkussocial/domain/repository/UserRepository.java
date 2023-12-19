package io.github.rafaelpino.quarkussocial.domain.repository;

import io.github.rafaelpino.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

//entender o aplicationscoped
@ApplicationScoped //essa anotation criar uma instancia da classe userrepository, dentro do container da injenção para criar aonde quiser
public class UserRepository implements PanacheRepository<User> {

}
