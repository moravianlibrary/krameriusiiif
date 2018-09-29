package cz.rumanek.kramerius.krameriusiiif.service;

import cz.rumanek.kramerius.krameriusiiif.dao.DocumentRepository;
import cz.rumanek.kramerius.krameriusiiif.dao.ImageInfoRepository;
import cz.rumanek.kramerius.krameriusiiif.dao.ImageInfoRepositoryImpl;
import cz.rumanek.kramerius.krameriusiiif.dto.DocumentDTO;
import cz.rumanek.kramerius.krameriusiiif.entity.KDocument;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.inject.Inject;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    private final DocumentRepository repository;

    private final ImageInfoRepository imageRepository;

    private ModelMapper modelMapper = new ModelMapper();

    @Inject
    public DocumentService(DocumentRepository repository, ImageInfoRepository imageInfoRepository) {
        this.repository = repository;
        this.imageRepository = imageInfoRepository;
    }

    ExecutorService executor = Executors.newFixedThreadPool(20);

    public Stream<DocumentDTO> findByParentPid(String parentPid) {


        long count = repository.countByParentPid(parentPid);
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<DocumentDTO>(count, Spliterator.SIZED) {
            int i = 0;
            final int PAGE_SIZE = 50;
            private Queue<DocumentDTO> queue = new LinkedBlockingQueue<>();

            @Override
            public synchronized boolean tryAdvance(Consumer<? super DocumentDTO> action) {
                if (queue.size() <= PAGE_SIZE / 2) {
                    List<KDocument> kDocumentList = repository.findByParentPid(parentPid, PageRequest.of(i++, PAGE_SIZE)).getContent();
                    List<DocumentDTO> documentDTOList = kDocumentList.stream()
                            .parallel()
                            .filter(document -> !document.getPid().equals(parentPid))
                            .map(entity -> {
                                //Info info = imageRepository.get(entity.getPid());
                                DocumentDTO dto = modelMapper.map(entity, DocumentDTO.class);
                                dto.setInfo(executor.submit(() -> imageRepository.get(entity.getPid())));
                                return dto;
                            })
                            .collect(Collectors.toList());
                    queue.addAll(documentDTOList);
                }
                DocumentDTO doc = queue.poll();
                if (doc != null) {
                    action.accept(doc);
                    return true;
                } else {
                    return false;
                }
            }
        }, false);
    }

    public Optional<KDocument> findByPid(String pid) {
        return repository.findByPid(pid);
    }
}
