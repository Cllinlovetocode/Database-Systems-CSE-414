SELECT y.name AS name, y.population AS population, 
       ARRAY_LENGTH((CASE 
                      WHEN y.religions IS MISSING THEN []
                      WHEN is_array(y.religions) THEN y.religions
                      ELSE [y.religions] 
                     END)) AS num_religions
FROM geo.world x, x.mondial.country y
ORDER BY y.name ASC;

[Result Size: 238 rows of {"num_religions":..., "country":..., "population":...}]
