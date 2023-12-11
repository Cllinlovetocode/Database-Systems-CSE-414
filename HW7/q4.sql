SELECT e.`#text` AS ethinic_group, SUM(float(y.`-percentage`)/100* float(y.`population` )) AS total_population, COUNT(*) AS num_countries
FROM geo.world x, x.mondial.country y, 
         CASE WHEN is_array(y.ethnicgroups) THEN y.ethnicgroups 
          ELSE [y.ethnicgroups] END AS e
GROUP BY e.`#text`
ORDER BY  e.`#text` ASC;

[Result Size: 262 of {"ethnic_group":..., "num_countries":..., "total_population":...}]
