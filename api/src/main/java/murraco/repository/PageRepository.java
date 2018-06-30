package murraco.repository;

import murraco.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface PageRepository extends JpaRepository<Page, Integer> {

    List<Page> findByOwnerUsername(String username);

    Optional<Page> findByName(String name);

}
