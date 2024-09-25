# Simplest app for OpenAI and Spring AI
This app uses a vector store for a semantic search 
RAG feature. The simple vector store is used because it
requires no installation. The vector store is refreshed with
data at the start of the application. The game rules are
in /resources folder as 'clubber-rules.txt', which is a 
club crawl game.

This is not how we'd normally manage vector stores obviously. 
The Spring ChatClient is configured to use our vector store
as an "advisor" for the request as the RAG feature. 

The framework will call the vector store by itself as part of 
the chat client request using the default vector search.

## Docker 

During the startup, when this module detects a ‘docker-compose.yml‘ in the root of your application folder, 
it uses that to start the containers before starting the application. 

The new module provides the following features:

When the application starts, containers/services defined in the compose file are started using 
the command “docker compose up“.

The service connection beans are created for each supported container.
When the application stops, containers/services defined in the compose file are shut down 
using the command “docker compose down“.

For now, we don't automate this process, since the docker
services can stay running as we start/stop the app a lot during
development and testing it out. 

## Build notes
Notice the use of latest possible version of Spring AI in pom.xml
this is so I can stay on top of the changes. Note that some of spring ai latest is
undocumented right now. 

I'm using the new Tika data loaders for vector stores.
https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/reader/tika/TikaDocumentReader.html

There is also new Readers in spring ai here, but I haven't found
examples of using them yet.
https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/reader/package-summary.html


## Quick Start

1. Start the application 
2. Use postman or curl to send a request to the application. There are samples below:

### Sample curl requests

```
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
   "question": "Can I barf at each bar so I can keep going to all bars?"
  }' \
  'http://localhost:8080/gamerules' 
```

```
curl --location 'http://localhost:8080/pgvector' \
--header 'Content-Type: application/json' \
--data '{
    "question": "Is the game of booboo required dice or cards to play it? If you don'\''t have rules for this game say so."
}'
```   


Example response: 
```
Based on the provided rules and context, the objective of the 
game of Clubber is to visit as many bars as possible, and the 
player who visits the most bars is the winner. There is no mention 
that reaching the last bar first guarantees a win. Therefore, simply 
getting to the last bar first does not determine the winner. The 
winner is the player who has visited the most bars without being disqualified.                                                                                  
```

---

# Next Steps

Here's a list of next steps to take this project further:

1. Add metadata or field to vector store so we can further qualify which game or attribute of the game in our search (e.g. player age range, name of game)
1. Copy and paste rules for a game that is super long to force chunking.
1. Add at least 10 more games with rules to the vector store.
1. Add a real vector store Postgres with pg_vector or other.
1. Can this app run easily using postgres and simple vector stores and I can use a parameter to REST API to select which one, like ?vector=pastgres or ?vector=simple so results can be compared easily. 
1. Load the postgres vector store independently from this application as a prerequisite.
1. Add a game selection feature to the application so you can specific which game your question is about.
1. Add a few more things in the vector store that are not games, can it detect the difference in searching?
1. Add support for loading from directory not part of /resources. Include both text and json files for input.