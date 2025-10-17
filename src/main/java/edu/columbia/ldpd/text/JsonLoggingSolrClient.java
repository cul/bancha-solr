package edu.columbia.ldpd.text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SolrJSONWriter;

public class JsonLoggingSolrClient extends SolrClient {

    private static final long serialVersionUID = 1L;
	private final SolrJSONWriter output;
    private boolean separate = false;

    public JsonLoggingSolrClient(String fileURL) throws IOException, URISyntaxException {
        File outputFile = new File(new URI(fileURL));
        this.output = new SolrJSONWriter(new FileWriter(outputFile)); 
    }

    private Map<String, Object> mapDocument(SolrInputDocument doc) {
        Map<String, Object> inputDocMap = new HashMap<>();
        for (String fieldName : doc.getFieldNames()) {
            inputDocMap.put(fieldName, doc.getFieldValue(fieldName));
        }
        return inputDocMap;
    }
    @Override
    public UpdateResponse add(SolrInputDocument doc) throws IOException {
    	if (separate) this.output.writeArraySeparator();
        this.output.writeObj(mapDocument(doc));
        this.separate = true;
        return new UpdateResponse();
    }

    @Override
    public UpdateResponse add(Collection<SolrInputDocument> docs) throws IOException {
        for (SolrInputDocument doc: docs) {
        	if (separate) this.output.writeArraySeparator();
            this.output.writeObj(mapDocument(doc));
            this.separate = true;
        };
        return new UpdateResponse();
    }

    @Override
    public UpdateResponse commit() {
        return new UpdateResponse();
    }

    @Override
	public void close() throws IOException {
		this.output.writeArrayCloser();
		this.output.close();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public NamedList<Object> request(SolrRequest request, String collection) throws SolrServerException, IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
