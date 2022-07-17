-- Analisar número total de artigos vendidos:
-- Entre 2022-03-01 e 2022-05-31, por dia da semana, por concelho, e no total

SELECT dia_semana, concelho, SUM(unidades) AS total_unidades
FROM vendas
WHERE make_date(CAST(ano AS int), CAST(mes AS int), CAST(dia_mes AS int)) BETWEEN '2022-03-01' AND '2022-05-31'
GROUP BY
	GROUPING SETS ((dia_semana), (concelho), ());
	
-- Analisar número total de artigos vendidos:
-- No distrito de Lisboa, por concelho, categoria, dia da semana e no total

SELECT concelho, cat, dia_semana, SUM(unidades) AS total_unidades
FROM vendas
WHERE distrito = 'Lisboa'
GROUP BY
	GROUPING SETS((concelho, cat, dia_semana), ());
	