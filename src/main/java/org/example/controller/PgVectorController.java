package org.example.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/pgvector")
public class PgVectorController {

    record RulesQuestionInput(String question) {}

    private final ChatClient aiClient;
    private final VectorStore vectorStore;

    @Value("classpath:/clubber-rules.txt")
    private Resource documentResource;


    public PgVectorController(ChatClient chatClient, VectorStore vectorStore) {
        this.aiClient = chatClient;
        this.vectorStore = vectorStore;
    }

    // Call this endpoint to load the vector store from the game rules file.
    @GetMapping(path="/load", produces = "text/plain")
    public String loadVectorStore() {
        Objects.requireNonNull(documentResource);
        if (!documentResource.exists()) {
            throw new RuntimeException("Game rules file not found: " + documentResource.getFilename());
        }

        TikaDocumentReader documentReader = new TikaDocumentReader(documentResource);
        TextSplitter textSplitter = new TokenTextSplitter();

        System.out.println("\n\n---\nReading game rules document: " + documentReader.get());

        vectorStore.accept(
                textSplitter.apply(
                        documentReader.get()));

        return "Vector store loaded from resource file: " + documentResource.getFilename();

    }

    /**
     * Ask about our custom game's rules, uses vector store to find answers.
     * @param rulesQuestion
     * @return
     */
    @PostMapping(produces = "text/plain")
    public String askAboutGameRules(@RequestBody RulesQuestionInput rulesQuestion) {
        return aiClient.prompt()
                .user(rulesQuestion.question())
                .advisors(new QuestionAnswerAdvisor(vectorStore))
                .call()
                .content();
    }


}
