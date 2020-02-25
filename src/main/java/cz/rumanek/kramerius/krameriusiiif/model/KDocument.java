package cz.rumanek.kramerius.krameriusiiif.model;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.Collections;
import java.util.List;

/*
 * @SolDocument value "collection" adds collection/core name to path of SOLR request.
 * If not defined, name of class is used/added.
 * There seems to be no global override for this behavior.
 * Collection name is set in application.properties as "kramerius.solr.core"
 * Because this annotation cannot read using SPEL directly from application.properties with $ symbol
 * (like in "${kramerius.iiif.endpoint}"), value is transfered by "solrCollectionName" bean in AppConfiguration
 */
/**
 * Defines fields to be retrieved form SOLR using "Spring Data Solr"
 */
@SolrDocument(collection = "#{@solrCollectionName}")
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

    @Field("rels_ext_index")
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

    public List<Integer> getRelsIndex() {
        return Collections.unmodifiableList(relsIndex);
    }
}
