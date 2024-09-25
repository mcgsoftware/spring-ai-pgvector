# Setup 
Getting docker and postgres working is a chore 
this video is pretty good at explaining the process
from a spring boot dev perspective. I recommend watching
it if you have any issues: https://www.youtube.com/watch?v=XDlgWyVfSMA

If the setup works, you should be running postgres in a docker container
plus a pgadmin app on port 5050. You won't run pgadmin or postgres this way
in a production or lowers type of environment, this is just for the
laptop developers.  

Once it starts up, point your browser to http://localhost:5050
and loging with your user email and password 'admin'. All those are settings
in the docker-compose.yaml file. 

1. Start docker desktop on your laptop. 
2. cd to where docker-compose.yaml is located in a terminal.
3. Run `docker compose up` to start the containers.
4. Verify the containers are running by running `docker ps` in a terminal.

You should see something like this below (note: I named this container
postgres-docker in my docker-compose.yaml file, so we know what it's going to be):
```
$  docker ps
CONTAINER ID   IMAGE                    COMMAND                  CREATED          STATUS          PORTS                    NAMES
08886d459a77   pgvector/pgvector:pg16   "docker-entrypoint.s…"   11 minutes ago   Up 11 minutes   0.0.0.0:5432->5432/tcp   postgres-docker

In the example above the docker container is running with name 'postgres
```

After these steps, if you want to use pgadmin (and you will). Follow 
instructions after these. 

5. run psql to connect to the database. 
```
Is it working?
Check the logs:
- docker logs -f postrgres-docker

List your current containers:
- docker ps -a or docker ps --last 1

Try connecting:
- docker exec -it {name-of-container} psql -U postgres
 In example above, container name is 'postgres-docker' (a little confusing the user name is postgres too)
 
   (note user and password are both postgres)
 $ docker exec -it postgres-docker psql -U postgres
 
 $ list the databases your postgres instance has running on it. 
   we should see the one we created via our docker compose file 'aidemo'
postgres=# 
postgres=# \l

Connect to aidemo database:
postgres=# \c aidemo
You are now connected to database "aidemo" as user "postgres".
aidemo=# 


   
 $ run a command to see if any tables. I created 
   a table 'fubar' earlier so this command showed it. 
aidemo=# 
aidemo=# \d
         List of relations
 Schema | Name  | Type  |  Owner   
--------+-------+-------+----------
 public | fubar | table | postgres
(1 row)

 
postgres=# 
- Use ctrl-d or type exit to exit psql
```

### Running psql commands to postgres inside docker from your shell
Note you can run this from shell this way:
Note: You can also just do while connected directly (the command above).

$ docker exec -it {name-of-container} psql -U postgres -c "{command}"
```
$ docker exec -it postgres-docker psql -U postgres -c "select * from fubar;"
 id  
-----
   1
 100
(2 rows)

```

## Setup pgadmin for your local postgres

See the video link above. At 14:00 minutes in the video, he shows how to setup pgadmin
using the UI. We will want to configure using the
parameters in our docker compose file. 

```
$ docker ps  # <= get the container id of pg_admin

$ docker inspect fbb6380bb75a | grep IPAddress

# It was 172.19.0.2 in my case.
```

# pg_vector and VectorStore

We are using 2 tables for vectors
* vector_store - the table required by spring ai. 
* demo_vector_store - a hot rodded vector table with added fields for hybrid query. 

Our hot rod vector table includes:
* train_code field to filter queries by train.
* created - timestamp the record was created
* updated - timestamp the record was last created.
* tags text[] - tags for searching content. need enum value range.
* source json - app-defined metadata for the original src of data.
* response json - app-defined short response data to use in case gen ai should not use the vector data for response.
* kvp hstore - hstore for key/value pairs. key and value must be strings.

The new table allows for simple filter by train code
since all queries at runtime will be using that. 

The timestamp fields are used so we know if the data
is stale or not, when we sync up with origin data sources. 

source is a json field that instructs us where the vector data
came from so we can refresh / sync the vector with origin data. 

response is for when we have either a hard-coded response we want to 
use if the vector search matched this record, or if we want to provide
metadata of where the app should get the data for the response from
(e.g. a url for an api call to get restaurant by id ). Note this
response could be also a Function call in the future (maybe). 