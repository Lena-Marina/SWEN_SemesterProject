CREATE TABLE mrp.users (
    user_id UUID PRIMARY KEY,
    username varchar(50) UNIQUE NOT NULL,
    email varchar(100),
    fav_genre varchar(100),
    hashed_pw varchar(255) NOT NULL
);

CREATE TABLE mrp.media_entry (
    media_id UUID PRIMARY KEY,
    creator_id UUID NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    type TEXT,
    release_year INTEGER,
    age_restriction INTEGER,

    CONSTRAINT fk_media_creator
        FOREIGN KEY (creator_id)
            REFERENCES mrp.users(user_id)
);

CREATE TABLE mrp.ratings (
    rating_id UUID PRIMARY KEY,
    creator_id UUID NOT NULL,
    media_id UUID NOT NULL,
    comment TEXT,
    stars INTEGER NOT NULL CHECK(stars BETWEEN 1 AND 5),
    timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
    confirmed BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_ratings_creator
        FOREIGN KEY (creator_id)
            REFERENCES mrp.users(user_id),

    CONSTRAINT fk_ratings_media
        FOREIGN KEY (media_id)
            REFERENCES mrp.media_entry(media_id)
);


CREATE TABLE mrp.genre (
    genre_id UUID PRIMARY KEY,
    name text UNIQUE NOT NULL
);

CREATE TABLE mrp.is_genre (
    media_id UUID NOT NULL,
    genre_id UUID NOT NULL,

    PRIMARY KEY (media_id, genre_id),

    CONSTRAINT fk_is_genre_media
        FOREIGN KEY (media_id)
            REFERENCES mrp.media_entry(media_id)
            ON DELETE CASCADE,

    CONSTRAINT fk_is_genre_genre
        FOREIGN KEY (genre_id)
            REFERENCES mrp.genre(genre_id)
            ON DELETE CASCADE
);

CREATE TABLE mrp.liked_by (
    user_id UUID NOT NULL,
    rating_id UUID NOT NULL,

    PRIMARY KEY (user_id, rating_id),

    CONSTRAINT fk_liked_by_user
        FOREIGN KEY (user_id)
            REFERENCES mrp.users(user_id)
            ON DELETE CASCADE,

    CONSTRAINT fk_liked_by_rating
        FOREIGN KEY (rating_id)
            REFERENCES mrp.ratings(rating_id)
            ON DELETE CASCADE
);

CREATE TABLE mrp.favorites (
    user_id UUID NOT NULL,
    media_id UUID NOT NULL,

    PRIMARY KEY (user_id, media_id),

    CONSTRAINT fk_favorites_user
        FOREIGN KEY (user_id)
            REFERENCES mrp.users(user_id)
            ON DELETE CASCADE,

    CONSTRAINT fk_favorites_media
        FOREIGN KEY (media_id)
            REFERENCES mrp.media_entry(media_id)
            ON DELETE CASCADE
);