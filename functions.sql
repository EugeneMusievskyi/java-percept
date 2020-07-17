 CREATE OR REPLACE FUNCTION bitcount(i bigint) RETURNS double precision AS $$
DECLARE n integer;
DECLARE amount double precision;
BEGIN
    amount := 0;
FOR n IN 1..16 LOOP
      amount := amount + ((i >> (n-1)) & 1);
END LOOP;
RETURN amount;
END
$$ LANGUAGE plpgsql;

 ------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION match_percent(a bigint, b bigint) RETURNS double precision AS $$
BEGIN
    RETURN 1 - bitcount(a # b) / 64;
END
$$ LANGUAGE plpgsql;

------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION search_image(imageHash bigint, threshold double precision)
    RETURNS table (imageId VARCHAR, matchPercent double precision, imageUrl varchar)
AS $$
BEGIN
    return QUERY (SELECT cast(i.id as VARCHAR) AS imageId, (1 - bitcount(i.hash # imageHash) / 64) AS matchPercent, i.url AS imageUrl
                  FROM images i WHERE (1 - bitcount(i.hash # imageHash) / 64) >= threshold);
END
$$
LANGUAGE plpgsql
