DROP PROCEDURE IF EXISTS add_star;
DROP PROCEDURE IF EXISTS add_movie;
DELIMITER //
CREATE PROCEDURE add_movie(m_title VARCHAR(100), m_year INTEGER, m_director VARCHAR(100),
							star_n VARCHAR(50), genre VARCHAR(32))
main: BEGIN
		DECLARE m_id varchar(100); # Movie ID
		DECLARE s_id varchar(100); # Star ID
		DECLARE g_id INT(11); # Genre ID
        DECLARE message varchar(65535);
		IF (m_title IS NULL OR m_year IS NULL OR m_director IS NULL OR star_n IS NULL OR genre IS NULL) # Are any of the parameters NULL?
		THEN
			SELECT 'One or more of the parameters is a NULL.' AS 'msgs';
			LEAVE main;
		END IF;
		IF (SELECT COUNT(*) FROM movies WHERE title = m_title AND m_year = year AND 
			director = m_director) # Does a record for the movie exist?
		THEN # Stop, because the movie already exists.
			SELECT 'The movie already exists' as 'msgs';
			LEAVE main;
		END IF;
		
        SET m_id =(select concat('tt0',(select CONVERT(substring(max(id),4), UNSIGNED)+1 FROM moviedb.movies)));

		INSERT INTO movies (id,title, year, director) VALUES(m_id,m_title, m_year, m_director);
        INSERT INTO ratings (movieId, rating,numVotes) VALUES (m_id,-1,0);
		SET message= 'The movie record was successfully created;' ;

		IF (SELECT(SELECT COUNT(*) FROM stars WHERE name = star_n) IS FALSE) # Does a record for the star not exist?
		THEN # Make a new record for the star.
			SET s_id =(select concat('nm',(select convert(substring(max(id),3),unsigned)+1 FROM moviedb.stars)));
			INSERT INTO stars (id,name) VALUES(s_id,star_n);
            INSERT INTO stars_in_movies(starId,movieId) VALUES(s_id,m_id);
			SET message =(SELECT CONCAT(message, ' A record for the star was created;'));
            SET message =(SELECT CONCAT(message, ' A record for the star_in_movies was created;'));
		else
			set s_id =(select id from stars where name=star_n);
			INSERT INTO stars_in_movies(starId,movieId) VALUES(s_id,m_id);
            SET message =(SELECT CONCAT(message, ' A record for the star_in_movies was created;'));
		END IF;
		
		IF (SELECT(SELECT COUNT(*) FROM genres WHERE name = genre) IS FALSE)
		THEN # Make a new record for the genre.
			SET g_id=(SELECT MAX(id)+1 FROM genres);
			INSERT INTO genres (id,name) VALUES(g_id,genre);
            INSERT INTO genres_in_movies(genreId,movieId) VALUES(g_id,m_id);
			SET message = (SELECT CONCAT(message,' A record for the genre was created;'));
            SET message =( SELECT CONCAT(message, ' A record for the genres_in_movies was created;'));
		else
			SET g_id=(select id from genres where name=genre);
            INSERT INTO genres_in_movies(genreId,movieId) VALUES(g_id,m_id);
            SET message =( SELECT CONCAT(message, ' A record for the genres_in_movies was created;'));
		END IF;
        SELECT message as 'msgs';
	END //
    
DELIMITER //
CREATE PROCEDURE add_star(s_name varchar(100),year int(11))
main:BEGIN
	DECLARE s_id varchar(100);
		IF s_name is NULL
        THEN
        SELECT 'Star name is a NULL.' AS 'msgs';
			LEAVE main;
		END IF;
	
		SET s_id =(select concat('nm',(select convert(substring(max(id),3),unsigned)+1 FROM moviedb.stars)));
        IF s_name != " " and year != -1
		THEN
			INSERT INTO stars (id,name,birthYear) VALUES(s_id,s_name,year);
            SELECT 'A record for the star was created' AS 'msgs';
            LEAVE main;
		END IF;
        IF s_name != " " THEN
			INSERT INTO stars (id,name) VALUES (s_id, s_name);
            SELECT 'A record for the star was created' AS 'msgs';
            LEAVE main;
		END IF;
END //
DELIMITER ;