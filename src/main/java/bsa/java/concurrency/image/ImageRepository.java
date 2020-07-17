package bsa.java.concurrency.image;

import bsa.java.concurrency.image.dto.SearchResultDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {
    // Функции находятся в файле functions.sql
    @Query(value =
            "SELECT * FROM search_image(:hash, :threshold)",
            nativeQuery = true)
    List<SearchResultDTO> searchImage(@Param("hash") long hash, @Param("threshold") double threshold);
}
