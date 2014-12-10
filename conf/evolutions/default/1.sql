# Shows table

# --- !Ups

CREATE TABLE Shows (
    seriesId long NOT NULL,
    seriesName varchar,
    PRIMARY KEY (seriesId)
);

# --- !Downs

DROP TABLE Shows;