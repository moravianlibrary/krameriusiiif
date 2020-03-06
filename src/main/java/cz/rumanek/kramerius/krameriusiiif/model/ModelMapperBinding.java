package cz.rumanek.kramerius.krameriusiiif.model;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ModelMapperBinding implements MappingUtil {

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public DocumentDTO mapFrom(KDocument kDocument) {
        return modelMapper.map(kDocument, DocumentDTO.class);
    }
}
