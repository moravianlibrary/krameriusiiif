package cz.rumanek.kramerius.krameriusiiif.entity;

import java.util.Collections;
import java.util.List;
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

    @Field("rels_ext_index")
    private List<Integer> relsIndex;

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

    public List<Integer> getRelsIndex() {
        return Collections.unmodifiableList(relsIndex);
    }
}
