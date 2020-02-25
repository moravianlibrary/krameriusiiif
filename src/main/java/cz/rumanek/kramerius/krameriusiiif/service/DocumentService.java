package cz.rumanek.kramerius.krameriusiiif.service;

import cz.rumanek.kramerius.krameriusiiif.model.DocumentDTO;
import cz.rumanek.kramerius.krameriusiiif.model.Info;
import cz.rumanek.kramerius.krameriusiiif.model.KDocument;
import cz.rumanek.kramerius.krameriusiiif.repository.DocumentRepository;
import cz.rumanek.kramerius.krameriusiiif.repository.ImageInfoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@Service
public class DocumentService {

    private final DocumentRepository repository;

    private final ImageInfoRepository imageRepository;

    private ModelMapper modelMapper = new ModelMapper();

    ExecutorService executor = Executors.newFixedThreadPool(20);

    @Autowired
    public DocumentService(DocumentRepository repository, ImageInfoRepository imageInfoRepository) {
        this.repository = repository;
        this.imageRepository = imageInfoRepository;
    }

    /**<pre>
     * Retrieves list of PID bound entities (pages) from repository (SOLR)
     * Sorts by RELS_EXT_INDEX (workaround - should be sorted at SOLR!)
     * Filters out parent PID entity (publication) that has no image information
     * Assigns image information to each PID entity (width/height)
     * </pre> See also @{@link Info}
     */
    public Stream<DocumentDTO> findByParentPid(String parentPid) {
        List<KDocument> documents = repository.findByParentPid(parentPid, PageRequest.of(0, 10000)).getContent();
        return documents.stream()
                //TODO-MR first index is not correct when document is indexed in article
                .sorted(Comparator.comparing(o -> o.getRelsIndex().get(0)))
                .filter(document -> !document.getPid().equals(parentPid))
                .map(entity -> {
            //Info info = imageRepository.get(entity.getPid());
            DocumentDTO dto = modelMapper.map(entity, DocumentDTO.class);
            dto.setInfo(executor.submit(() -> imageRepository.get(entity.getPid())));
            return dto;
        });
    }

    /**
     * TODO => SMAZAT
     * TODO-MR
     * This cannot be used, because rels_ext_index field in SOLR is multivalue. This type of field can be sorted only when are enabled docValues.
     *
     * There is one another problem - which value use for sorting. This has some solution like additional value (ask Alberto).
     */
//    @SuppressWarnings("lazy loading will be possible with new index")
//    private Stream<DocumentDTO> lazyFindByParentPid(String parentPid) {
//        long count = repository.countByParentPid(parentPid);
//        return StreamSupport.stream(new Spliterators.AbstractSpliterator<DocumentDTO>(count, Spliterator.SIZED) {
//            int i = 0;
//            final int PAGE_SIZE = 50;
//            private Queue<DocumentDTO> queue = new LinkedBlockingQueue<>();
//
//            @Override
//            public synchronized boolean tryAdvance(Consumer<? super DocumentDTO> action) {
//                if (queue.size() <= PAGE_SIZE / 2) {
//                    List<KDocument> kDocumentList = repository.findByParentPid(parentPid, PageRequest.of(i++, PAGE_SIZE)).getContent();
//                    List<DocumentDTO> documentDTOList = kDocumentList.stream()
//                            .parallel()
//                            .filter(document -> !document.getPid().equals(parentPid))
//                            .map(entity -> {
//                                //Info info = imageRepository.get(entity.getPid());
//                                DocumentDTO dto = modelMapper.map(entity, DocumentDTO.class);
//                                dto.setInfo(executor.submit(() -> imageRepository.get(entity.getPid())));
//                                return dto;
//                            })
//                            .collect(Collectors.toList());
//                    queue.addAll(documentDTOList);
//                }
//                DocumentDTO doc = queue.poll();
//                if (doc != null) {
//                    action.accept(doc);
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        }, false);
//    }

    /**
     * Retrieves single PID entity from repository
     * If found, tries to cache its image (if any) - useless!
     * Then returns assembled DocumentDTO (with or without image info)
     */
    public Optional<DocumentDTO> findByPid(String pid) {
        Optional<KDocument> doc = repository.findByPid(pid);
        if (doc.isPresent()) {
            Info info = imageRepository.get(doc.get().getPid());
            DocumentDTO dto = modelMapper.map(doc.get(), DocumentDTO.class);
            if (info != null) {
                CompletableFuture<Info> futureInfo = new CompletableFuture<>();
                futureInfo.complete(info);
                dto.setInfo(futureInfo);
            }
            return Optional.of(dto);
        } else {
            return Optional.empty();
        }
    }
}
