import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;

import java.io.IOException;

/**
 * Created by 浩 on 2015-10-23 0023.
 */
public class SolrTest {

    public static void main(String[] args) throws IOException, SolrServerException {
        String url = "http://localhost:8983/solr/";
        SolrClient client = new HttpSolrClient(url);
        SolrInputDocument document = new SolrInputDocument();
        document.addField("test_s", "哈哈哈22");
        document.addField("id", "00588");
        client.add("main", document);
        SolrParams params = new SolrQuery("test_s:*哈*");
        QueryResponse response = client.query("main", params);
        System.out.println(response.getResponse().get("response"));
        client.commit("main");
    }
}
