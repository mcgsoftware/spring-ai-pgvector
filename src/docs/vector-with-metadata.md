# Vectors and metadata for searching with


Spring AI's vector store implementations can indeed store key/value pair data alongside vectors. This is a common requirement in many real-world applications, especially in scenarios like product search where you need both semantic similarity (via vectors) and structured data filtering (like price).

Spring AI supports this through metadata associated with each document. Here's how you can approach this:

1. Storing Data:
   When you add documents to the vector store, you can include metadata with each document. This metadata can contain key/value pairs like price, category, or any other structured data you want to associate with the vector.

2. Querying:
   When performing a similarity search, you can use both vector similarity and metadata filtering to get your results.

Let's look at a concrete example of how you might implement this using Spring AI:



```java
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductSearchService {

    private final VectorStore vectorStore;
    private final EmbeddingClient embeddingClient;

    public ProductSearchService(VectorStore vectorStore, EmbeddingClient embeddingClient) {
        this.vectorStore = vectorStore;
        this.embeddingClient = embeddingClient;
    }

    public void addProduct(String description, double price, String category) {
        List<Double> embedding = embeddingClient.embed(description);
        Document document = new Document(description, Map.of(
            "price", price,
            "category", category
        ));
        vectorStore.add(List.of(embedding), List.of(document));
    }

    public List<Document> searchProducts(String query, Double maxPrice, String category) {
        List<Double> queryEmbedding = embeddingClient.embed(query);

        SearchRequest searchRequest = SearchRequest.query(queryEmbedding)
            .withTopK(10)
            .withSimilarityThreshold(0.7f)
            .withFilterExpression(buildFilterExpression(maxPrice, category));

        return vectorStore.search(searchRequest);
    }

    private String buildFilterExpression(Double maxPrice, String category) {
        StringBuilder filter = new StringBuilder();

        if (maxPrice != null) {
            filter.append("price <= ").append(maxPrice);
        }

        if (category != null && !category.isEmpty()) {
            if (filter.length() > 0) {
                filter.append(" AND ");
            }
            filter.append("category = '").append(category).append("'");
        }

        return filter.toString();
    }
}

```

Let's break down this example:

1. `addProduct` method:
    - Creates an embedding for the product description.
    - Creates a `Document` with the description as content and price and category as metadata.
    - Adds the embedding and document to the vector store.

2. `searchProducts` method:
    - Creates an embedding for the search query.
    - Builds a `SearchRequest` with:
        - The query embedding for semantic similarity.
        - A top-k value to limit the number of results.
        - A similarity threshold to ensure quality matches.
        - A filter expression based on price and category.
    - Performs the search using the vector store.

3. `buildFilterExpression` method:
    - Constructs a filter expression string based on the provided price and category.
    - This allows for filtering results based on metadata.

Key points to note:

1. Metadata Storage: The `Document` class allows you to store arbitrary metadata as key-value pairs.

2. Filtering: The `SearchRequest` class supports filter expressions, allowing you to combine vector similarity search with metadata filtering.

3. Flexibility: You can easily extend this to include more metadata fields or more complex filtering logic.

To use this in your Spring AI application:

1. Ensure your chosen vector store implementation supports metadata filtering. For example, the Postgres vector store in Spring AI supports this well.

2. When adding documents to the store, include all relevant metadata.

3. When searching, use both vector similarity and metadata filters as needed.

4. Be aware of the performance implications of complex metadata filters, especially with large datasets.

This approach allows you to leverage both the power of vector similarity for semantic search and the precision of structured data filtering. It's particularly useful in scenarios like e-commerce search, where you need to balance relevance (via vector similarity) with hard constraints like price ranges or product categories.

Remember that the exact syntax for filter expressions might vary depending on the specific vector store implementation you're using. Always refer to the documentation of your chosen vector store for the most accurate information on filter syntax and capabilities.