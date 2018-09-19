package cz.rumanek.kramerius.krameriusiiif.service;

import cz.rumanek.kramerius.krameriusiiif.dao.DocumentRepository;
import cz.rumanek.kramerius.krameriusiiif.dao.ImageInfoRepository;
import cz.rumanek.kramerius.krameriusiiif.dao.ImageInfoRepositoryImpl;
import cz.rumanek.kramerius.krameriusiiif.dto.DocumentDTO;
import cz.rumanek.kramerius.krameriusiiif.entity.Info;
import cz.rumanek.kramerius.krameriusiiif.entity.KDocument;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.inject.Inject;
import org.glassfish.hk2.classmodel.reflect.util.LinkedQueue;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    private final DocumentRepository repository;

    private final ImageInfoRepository imageRepository = new ImageInfoRepositoryImpl();

    private ModelMapper modelMapper = new ModelMapper();

    @Inject
    public DocumentService(DocumentRepository repository) {
        this.repository = repository;
    }

    public Stream<DocumentDTO> findByParentPid(String parentPid) {

        Supplier<DocumentDTO> childrenSupplier = new Supplier<DocumentDTO>() {
            int i = 0;
            long count = repository.countByParentPid(parentPid);
            final int PAGE_SIZE = 10;
            private Queue<DocumentDTO> queue = new LinkedBlockingQueue<>();

            @Override
            public synchronized DocumentDTO get() {
                if (queue.size() <= PAGE_SIZE/2 && i < count) {
                    List<KDocument> kDocumentList = repository.findByParentPid(parentPid, PageRequest.of(i++, 10)).getContent();
                    List<DocumentDTO> documentDTOList = kDocumentList.stream()
                            .parallel().map(entity -> {
                                Info info = imageRepository.get(entity.getPid());
                                DocumentDTO dto = modelMapper.map(entity, DocumentDTO.class);
                                if (info != null) {
                                    dto.setWidth(info.getWidth());
                                    dto.setHeight(info.getHeight());
                                }
                                return dto;
                            })
                            .collect(Collectors.toList());
                    queue.addAll(documentDTOList);
                }

                return queue.poll();
            }
        };

        long count = repository.countByParentPid(parentPid);
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<DocumentDTO>(count, Spliterator.SIZED) {
            long remaining=count;

            @Override
            public boolean tryAdvance(Consumer<? super DocumentDTO> action) {
                if(remaining<=0) return false;
                remaining--;
                action.accept(childrenSupplier.get());
                return true;
            }
        }, false).filter(document -> !document.getPid().equals(parentPid));
    }

    public Optional<KDocument> findByPid(String pid) {
        return repository.findByPid(pid);
    }
}
