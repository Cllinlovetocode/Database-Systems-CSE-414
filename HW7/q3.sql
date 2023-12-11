SELECT r.`#text` AS religion, COUNT(*) AS num_countries
FROM geo.world x, x.mondial.country y, 
         CASE WHEN is_array(y.religions) THEN y.religions 
          ELSE [y.religions] END AS r
GROUP BY r.`#text`
ORDER BY num_countries DESC;

[Result size: 38 of {"religion':..., "num_countries":...}]
