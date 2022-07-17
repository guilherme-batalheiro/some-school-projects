DROP VIEW IF EXISTS vendas;

CREATE VIEW vendas(
    ean, 
    cat, 
    ano, 
    trimestre, 
    mes, 
    dia_mes, 
    dia_semana, 
    distrito, 
    concelho, 
    unidades
)
AS	
SELECT ean,
        cat,
        EXTRACT(YEAR FROM instante),
        EXTRACT(QUARTER FROM instante),
        EXTRACT(MONTH FROM instante),
        EXTRACT(DAY FROM instante),
        EXTRACT(DOW FROM instante),
        distrito, 
        concelho, 
        unidades 
FROM (
    evento_reposicao NATURAL JOIN (
        instalada_em INNER JOIN ponto_de_retalho 
            ON instalada_em.local = ponto_de_retalho.nome
        )
    )
NATURAL JOIN produto;