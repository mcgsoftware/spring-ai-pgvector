To set up a PostgreSQL vector store using pg_vector for your Spring AI application, you'll need to use a different implementation than the SimpleVectorStore. Spring AI doesn't have a built-in PostgreSQL vector store implementation, but we can create one using the PGvector extension.

Here's how you can set up a PostgreSQL vector store:

1. First, ensure you have the PGvector extension installed in your PostgreSQL database.

2. Add the necessary dependencies to your project. You'll need the PostgreSQL JDBC driver and potentially a vector library like JBLAS for vector operations.

3. Create a custom implementation of the VectorStore interface that uses PostgreSQL with PGvector.

Here's an example of how you might set this up:



```java
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class PostgresVectorStore implements VectorStore {

    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingModel embeddingModel;

    public PostgresVectorStore(DataSource dataSource, EmbeddingModel embeddingModel) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.embeddingModel = embeddingModel;
        initializeTable();
    }

    private void initializeTable() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS vector_store (
                id SERIAL PRIMARY KEY,
                content TEXT,
                embedding VECTOR(1536)
            )
        """);
    }

    @Override
    public void add(List<Document> documents) {
        for (Document doc : documents) {
            List<Double> embedding = embeddingModel.embed(doc.getContent()).getVector();
            jdbcTemplate.update(
                "INSERT INTO vector_store (content, embedding) VALUES (?, ?::vector)",
                doc.getContent(),
                embedding.toString().replaceAll("[\\[\\]]", "")
            );
        }
    }

    @Override
    public List<Document> similaritySearch(String query) {
        List<Double> queryEmbedding = embeddingModel.embed(query).getVector();
        String embeddingString = queryEmbedding.toString().replaceAll("[\\[\\]]", "");
        
        return jdbcTemplate.query(
            """
            SELECT content, 1 - (embedding <=> ?::vector) as similarity
            FROM vector_store
            ORDER BY similarity DESC
            LIMIT 5
            """,
            (rs, rowNum) -> new Document(rs.getString("content"), Map.of("similarity", rs.getDouble("similarity"))),
            embeddingString
        );
    }

    // Implement other methods of the VectorStore interface as needed
}

```

Now, you can use this PostgresVectorStore in your Spring configuration:

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;

@Configuration
public class VectorStoreConfig {

    @Bean
    VectorStore vectorStore(DataSource dataSource, EmbeddingModel embeddingModel) {
        return new PostgresVectorStore(dataSource, embeddingModel);
    }
}

```

This implementation:

1. Creates a table named `vector_store` with columns for id, content, and the vector embedding.
2. Implements the `add` method to insert documents and their embeddings into the database.
3. Implements the `similaritySearch` method to find similar documents using the cosine similarity operator (`<=>`) provided by PGvector.

Some important notes:

1. This example assumes your embeddings have 1536 dimensions. Adjust the `VECTOR(1536)` part if your embeddings have a different dimension.
2. The similarity search uses cosine similarity. You might need to adjust this based on your specific requirements.
3. Error handling, transaction management, and optimization are not included in this basic example. You should add these for a production-ready implementation.
4. Make sure your PostgreSQL database has the PGvector extension enabled.

To use this, you'll need to ensure your application.properties (or application.yml) file is correctly configured to connect to your PostgreSQL database, as we discussed in the previous response.

Would you like me to explain any part of this implementation in more detail?