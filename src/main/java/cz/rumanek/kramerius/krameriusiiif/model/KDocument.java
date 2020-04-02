package cz.rumanek.kramerius.krameriusiiif.model;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.List;

import static cz.rumanek.kramerius.krameriusiiif.config.Constants.SOLR_COLLECTION_BEAN;

/**
 * Defines fields to be retrieved form SOLR using "Spring Data Solr"<br>
 * <pre>
 *{@literal @}SolDocument value "collection" adds collection/core name to path of SOLR request.
 * If not defined, name of class is used/added.
 * Collection name is set by <b>"kramerius.solr_core"</b> in application.properties
 * and transferred from KrameriusConfig bean <b>"solr_collection"</b>
 * </pre>
 */
@SolrDocument(collection = SOLR_COLLECTION_BEAN)
public final class KDocument {

    @Id
    @Field("PID")
    @Indexed
    private String pid;

    @Field("parent_pid")
    private String parentPid;

    @Field("dc.title")
    private String label;

    @Field("fedora.model")
    private String model;

    @Field(value = "rels_ext_index")
    private List<Integer> relsIndex;

    public String getPid() {
        return pid;
    }

    public String getLabel() {
        return label;
    }

    public String getModel() {
        return model;
    }

    @Override
    public String toString() {
        return "KDocument{" +
                "pid='" + pid + '\'' +
                '}';
    }

    public Integer getRelsIndex() {
        return relsIndex.get(0);
    }
}
