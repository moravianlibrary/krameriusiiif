package cz.rumanek.kramerius.krameriusiiif.service;

import cz.rumanek.kramerius.krameriusiiif.model.*;
import cz.rumanek.kramerius.krameriusiiif.repository.DocumentRepository;
import cz.rumanek.kramerius.krameriusiiif.repository.ImageInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@Service
public class DocumentService {

    private final DocumentRepository repository;
    private final ImageInfoRepository imageRepository;
    private final MappingUtil mappingUtil;

    ExecutorService executor = Executors.newCachedThreadPool();

    @Autowired
    public DocumentService(DocumentRepository repository,
                           ImageInfoRepository imageInfoRepository,
                           MappingUtil mappingUtil) {
        this.repository = repository;
        this.imageRepository = imageInfoRepository;
        this.mappingUtil = mappingUtil;
    }

    /**<pre>
     * Retrieves list of PID bound entities (pages) from repository (SOLR)
     * Sorts by RELS_EXT_INDEX (workaround - should be sorted at SOLR!)
     * Filters out parent PID entity (publication) that has no image information
     * Parent PID perhaps should not be supplied by SOLR in this context
     * Assigns image information to each PID entity (width/height)
     * </pre> See also @{@link Info}<br>
     *
     * Because rels_ext_index field in SOLR is multivalue. This type of field can be sorted only when are enabled docValues.
     * There is one another problem - which value use for sorting. This has some solution like additional value (ask Alberto).
     */
    public Stream<DocumentEntity> findAllByParentPid(String parentPid) {
        Page<KDocument> documents = repository.findByParentPid(parentPid, PageRequest.of(0, 10000));
        return documents.get().parallel()
                //TODO-MR first index is not correct when document is indexed in article
                .filter(kDocument -> !kDocument.getPid().equals(parentPid))  //dont filter out parent in future
                .sorted(Comparator.comparing(KDocument::getRelsIndex))
                .map(kDocument -> {
                    DocumentDTO dto = mappingUtil.mapFrom(kDocument);
                    tryAddImageInfoFor(dto);
                    return dto;
                });
    }

    /**
     * Retrieves single PID entity from repository
     * Then returns assembled DocumentDTO (with or without image info)
     */
    public Optional<DocumentEntity> findByPid(String pid) {
        Optional<KDocument> kDocument = repository.findByPid(pid);
        return kDocument.map(mappingUtil::mapFrom);
    }

    /**
     * Requests lazily image info using Future
     */
    private void tryAddImageInfoFor(DocumentDTO dto) {
        if (dto.isPage()) {
            dto.setInfo(executor.submit(() -> imageRepository.getInfo(dto.getPid())));
        }
    }

    public Stream<DocumentEntity> getPagesFor(String pid) {
        return findAllByParentPid(pid).filter(DocumentEntity::isPage);
    }

    public Stream<DocumentEntity> getCollections(String pid) {
        return findAllByParentPid(pid).filter(document -> !document.isPage());
    }
}
