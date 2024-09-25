package org.example;


import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import java.util.Objects;


@SpringBootApplication
public class Application {

    // This is the file that contains the game rules
    // TODO: This is not a scalable way to do this. Loading a vector store
    // should be an independent, stand-alone process done before this app runs.
    //
    @Value("classpath:/clubber-rules.txt")
    private Resource documentResource;


    public Application() {}

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);


        System.out.print("Hello World");
    }


//    // Load game rules from resource file.
//    @Bean
//    ApplicationRunner applicationRunner(ApplicationContext cxt) {
//
//
////        ApplicationRunner applicationRunner(VectorStore vectorStore) {
//
//        return args -> {
//            System.out.println("Loading game rules from resource file into pg_vector");
//
//            VectorStore vs = applicationContext.getBean(VectorStore.class);
//
//            Objects.requireNonNull(documentResource);
//            if (!documentResource.exists()) {
//                throw new RuntimeException("Game rules file not found: " + documentResource.getFilename());
//            }
//
//            TikaDocumentReader documentReader = new TikaDocumentReader(documentResource);
//            TextSplitter textSplitter = new TokenTextSplitter();
//
//            System.out.println("\n\n---\nReading game rules document: " + documentReader.get());
//
////            vectorStore.accept(
////                    textSplitter.apply(
////                            documentReader.get()));
//        };
//    }

    // Load game rules from resource file.
//    @Bean
//    ApplicationRunner pgVectorLoad(VectorStore vectorStore) {
//        return args -> {
//            Objects.requireNonNull(documentResource);
//            if (!documentResource.exists()) {
//                throw new RuntimeException("Game rules file not found: " + documentResource.getFilename());
//            }
//
//            TikaDocumentReader documentReader = new TikaDocumentReader(documentResource);
//            TextSplitter textSplitter = new TokenTextSplitter();
//
//            System.out.println("\n\n---\nReading game rules document: " + documentReader.get());
//
//            vectorStore.accept(
//                    textSplitter.apply(
//                            documentReader.get()));
//        };
//    }

}