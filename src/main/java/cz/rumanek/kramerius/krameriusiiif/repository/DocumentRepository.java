package cz.rumanek.kramerius.krameriusiiif.repository;

import cz.rumanek.kramerius.krameriusiiif.model.KDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface DocumentRepository extends PagingAndSortingRepository<KDocument, Long> {
    long count();

    Page<KDocument> findAll(Pageable pageable);

    long countByParentPid(String parentPid);

    Page<KDocument> findByParentPid(String parentPid, Pageable pageable);

    Optional<KDocument> findByPid(String pid);
}