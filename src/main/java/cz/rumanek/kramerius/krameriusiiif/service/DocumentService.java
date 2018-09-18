package cz.rumanek.kramerius.krameriusiiif.service;

import cz.rumanek.kramerius.krameriusiiif.dao.DocumentRepository;
import cz.rumanek.kramerius.krameriusiiif.dto.KDocument;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.inject.Inject;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    private final DocumentRepository repository;

    @Inject
    public DocumentService(DocumentRepository repository) {
        this.repository = repository;
    }

    public Stream<KDocument> findByParentPid(String parentPid) {
        Supplier<KDocument> childrenSupplier = new Supplier<KDocument>() {
            int i = 0;

            @Override
            public KDocument get() {
                return repository.findByParentPid(parentPid, PageRequest.of(i++, 1)).getContent().get(0);
            }
        };

        long count = repository.countByParentPid(parentPid);
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<KDocument>(count, Spliterator.SIZED) {
            long remaining=count;

            @Override
            public boolean tryAdvance(Consumer<? super KDocument> action) {
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
