package agh.oot.librarby.author.repository;

import agh.oot.librarby.author.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    /**
     * Case-insensitive "contains" search across firstName, middleName, lastName.
     * Middle/last name may be NULL in DB.
     */
    @Query("""
        select a
        from Author a
        where
            lower(a.firstName) like lower(concat('%', :q, '%'))
            or lower(coalesce(a.middleName, '')) like lower(concat('%', :q, '%'))
            or lower(coalesce(a.lastName, '')) like lower(concat('%', :q, '%'))
    """)
    List<Author> searchByName(@Param("q") String q);
}
