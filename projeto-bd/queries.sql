-- Qual o nome do retalhista (ou retalhistas) responsáveis pela reposição do 
-- maior número de categorias? 

SELECT tb_nr_cat.nome 
FROM (
    SELECT nome, tin, COUNT(DISTINCT nome_cat) AS nr_cat
    FROM retalhista NATURAL JOIN responsavel_por 
    GROUP BY tin
) AS tb_nr_cat
WHERE tb_nr_cat.nr_cat = (
    SELECT MAX(tb_nr_cat_in.nr_cat) 
    FROM (
        SELECT COUNT(DISTINCT nome_cat) AS nr_cat 
        FROM responsavel_por 
        GROUP BY tin
    ) AS tb_nr_cat_in
);

-- Qual o nome do ou dos retalhistas que são responsáveis por todas as 
-- categorias simples? 

SELECT R.nome 
FROM retalhista R, 
(
    SELECT DISTINCT RP.tin
    FROM responsavel_por RP
    WHERE NOT EXISTS (
        SELECT nome
        FROM categoria_simples
        EXCEPT
        SELECT nome_cat
        FROM (responsavel_por R JOIN categoria_simples C 
            ON R.nome_cat = C.nome) AS AC 
        WHERE AC.tin = RP.tin 
    )
) AS N
WHERE R.tin = N.tin;

-- Quais os produtos (ean) que nunca foram repostos?

SELECT ean FROM produto 
EXCEPT
SELECT DISTINCT ean FROM evento_reposicao;

-- Quais os produtos (ean) que foram repostos sempre pelo mesmo retalhista? 

SELECT ean FROM evento_reposicao 
GROUP BY ean 
HAVING COUNT(DISTINCT tin) = 1;
