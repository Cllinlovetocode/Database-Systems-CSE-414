
SELECT DISTINCT y.name as country, y.`-car_code` AS country_code, s AS seaOne
FROM geo.world x, x.mondial.country y
LET s = (SELECT y1.name AS seaTwo
		FROM geo.world x, x.mondial.country y1, x.mondial.sea s1,
			      split(s1.`-country`, " ") as border
		WHERE y1.`-car_code` = border AND y = y1)
WHERE array_count(s) >= 2
ORDER BY array_count(s) DESC;

[Result Size: 74 rows of {"country_code":..., "country_name":..., "seas": [{"sea":...}, {"sea":...}, ...]}]
