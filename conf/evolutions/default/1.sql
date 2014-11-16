# Shows table

# --- !Ups

CREATE TABLE Shows (
    seriesId long NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (seriesId)
);

# --- !Downs

DROP TABLE Shows;