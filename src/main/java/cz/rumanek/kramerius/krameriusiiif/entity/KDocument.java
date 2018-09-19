package cz.rumanek.kramerius.krameriusiiif.entity;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(collection = "v5.0")
public class KDocument {
    @Id
    @Field("PID")
    @Indexed
    private String pid;

    @Field("parent_pid")
    private String parentPid;

    @Field("dc.title")
    private String label;

    public String getPid() {
        return pid;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "KDocument{" +
                "pid='" + pid + '\'' +
                '}';
    }
}
