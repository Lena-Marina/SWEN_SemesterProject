INSERT INTO mrp.users (user_id, username, email, fav_genre, hashed_pw)
    VALUES
        ('11111111-1111-1111-1111-111111111111',
         'user1',
         'user1@email.email',
         'ANIMATION',
         'passwort1234'),
        ('22222222-2222-2222-2222-222222222222',
         'user2',
         'user2@email.email',
         'HORROR',
         'passwort1234');

INSERT INTO mrp.media_entry (media_id, creator_id, title, description, type, release_year, age_restriction)
    VALUES
        ('22222222-2222-2222-2222-222222222222',
         '11111111-1111-1111-1111-111111111111',
         'Persepolis',
         'Persepolis ist ein französischer Zeichentrickfilm aus dem Jahr 2007. Basierend auf der gleichnamigen Graphic Novel von Marjane Satrapi (* 1969) erzählt Persepolis die Kindes- und Jugendgeschichte der Regisseurin während und nach der Islamischen Revolution (1979) im Iran.',
         'movie',
         2007,
         12
         ),
        ('11111111-1111-1111-1111-111111111111',
         '11111111-1111-1111-1111-111111111111',
         'Ame & Yuki',
         'Die Studentin Hana verliebt sich in einen Mitstudenten, der sich fließend von einem Menschen in einen Wolf verwandeln kann. Als dieser stirbt, muss sie alleine für die beiden gemeinsamen Kinder sorgen.',
         'movie',
         2012,
         0
        ),

        ('33333333-3333-3333-3333-333333333333',
         '22222222-2222-2222-2222-222222222222',
         'Spuk in Bly Manor',
         'Die Amerikanerin Dani Clayton nimmt, nachdem sie ihren Verlobten Edmund bei einem Autounfall verloren hat, in London bei dem Anwalt Henry Wingrave eine Stellung als Au-pair an',
         'series',
         2020,
         16
        );

INSERT INTO mrp.is_genre (media_id, genre_id)
    VALUES
        ('11111111-1111-1111-1111-111111111111',
         'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
        ('11111111-1111-1111-1111-111111111111',
         '88888888-8888-8888-8888-888888888888'),
        ('22222222-2222-2222-2222-222222222222',
         '99999999-9999-9999-9999-999999999999'),
        ('22222222-2222-2222-2222-222222222222',
         'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
        ('33333333-3333-3333-3333-333333333333',
         '33333333-3333-3333-3333-333333333333'),
        ('33333333-3333-3333-3333-333333333333',
         '11111111-1111-1111-1111-111111111111');

INSERT INTO mrp.ratings (rating_id, creator_id, media_id, comment, stars, confirmed)
    VALUES
        ('11111111-1111-1111-1111-111111111111',
         '11111111-1111-1111-1111-111111111111',
         '11111111-1111-1111-1111-111111111111',
         'eine herzerwärmende Geschichte',
         5,
         true
         ),
        ('22222222-2222-2222-2222-222222222222',
         '11111111-1111-1111-1111-111111111111',
         '22222222-2222-2222-2222-222222222222',
         'hat mir gut gefallen, außer, dass sie Wien nicht mag',
         4,
         false
        );

INSERT INTO mrp.liked_by (user_id, rating_id)
    VALUES ('22222222-2222-2222-2222-222222222222',
            '22222222-2222-2222-2222-222222222222');

INSERT INTO mrp.favorites(user_id, media_id)
    VALUES('11111111-1111-1111-1111-111111111111',
           '11111111-1111-1111-1111-111111111111');




