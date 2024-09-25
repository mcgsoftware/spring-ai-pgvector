


-- Sample data for demo vector store table
INSERT INTO demo_vector_store (
    train_code,
    tags,
    source,
    response,
    kvp,
    content
) VALUES (
    'A01',
    ARRAY['instruction', 'tutorial'],
    '{"source_type": "manual", "source_id": "12345"}',
    '{"fallback_response": "Please refer to the user manual for details."}',
    '"language"=>"English", "format"=>"text"',
    'This is a tutorial content on how to use the train safety system.'
);

INSERT INTO demo_vector_store (
    train_code,
    tags,
    source,
    response,
    kvp,
    content
) VALUES (
    'B02',
    ARRAY['alert', 'safety'],
    '{"source_type": "alert_system", "alert_id": "67890"}',
    '{"fallback_response": "Please follow the safety instructions immediately."}',
    '"priority"=>"high", "region"=>"North"',
    'Emergency safety alert for train B02. Evacuate immediately.'
);

-- train_code of 000 means this applies to all trains.
INSERT INTO demo_vector_store (
    train_code,
    tags,
    source,
    response,
    kvp,
    content
) VALUES (
    'ALL',
    ARRAY['general', 'info'],
    '{"source_type": "database", "source_id": "54321"}',
    '{"fallback_response": "Everybody has to pay fare. No exceptions."}',
    '"category"=>"general", "version"=>"1.0"',
    'Everybody has to pay fare. No exceptions. This is General information applicable to all trains.'
);


-- train_code of 000 means this applies to all trains.
INSERT INTO demo_vector_store (
    train_code,
    tags,
    source,
    response,
    kvp,
    content
) VALUES (
    'ALL',
    ARRAY['games', 'info'],
    '{"source_type": "file", "source_id": "clubber_game_rules.txt"}',
    '{"fallback_response": "Just had a drink and a good time."}',
    '"category"=>"games", "who"=>"passengers"',
    '$$
    * How to play the game of Clubber

     The game of Clubber is a simple game that can be played by two or more players.
     The game is played with a list of bars for players to visit.
     The object of the game is to visit as many bars as possible and the player who
     visits the most bars is the winner.


     The game of Clubber is played as follows:
     * All players must be 21 and over.
     * Players agree on the list of bars to visit.
     * Bars must be within walking distance of each other.
     * Players go together to the first bar on the list.
     * Players must order a drink at each bar.
     * Players must finish their drink before moving on to the next bar.
     * Players must visit all the bars on the list in order.
     * The player who visits the most bars is the winner.
     * If a player vomits or gets sick and cannot continue, they are disqualified.
     * If a player is too drunk to continue, they are disqualified.
     * If a player is arrested, they are disqualified.
     $$'
);

