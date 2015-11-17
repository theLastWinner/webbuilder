import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by 浩 on 2015-10-23 0023.
 */
public class SolrTest {
    static String url = "http://202.98.57.19:9006/solr";
    static SolrClient client = new HttpSolrClient(url);

    public static void main(String[] args) throws IOException, SolrServerException {
//        SolrInputDocument document = new SolrInputDocument();
//        document.addField("test_s", "哈哈哈22");
//        document.addField("id", "00588");
        // client.add("main", document);
        SolrQuery params = new SolrQuery("content_s:*哈*");
        params.addHighlightField("content_s");
        QueryResponse response = client.query("main", params);
        System.out.println(response.getResponse().get("response").getClass());
        // CoreAdminRequest.createCore("solrj_test","/solr/core/solrj_test",client);
        // client.commit("main");

    }

    @Test
    public void testCreateCore() throws IOException, SolrServerException {
        CoreAdminRequest.createCore("solrj_test", "/solr/core/solrj_test", client);
        client.commit();
    }
}
