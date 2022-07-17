--------------------------------------------------------------------------------
-- (RI-1) Uma Categoria não pode estar contida em si própria
--------------------------------------------------------------------------------
DROP TRIGGER IF EXISTS trg_contida_em_loop ON tem_outra;

CREATE OR REPLACE FUNCTION trg_contida_em_loop()
RETURNS TRIGGER AS
$$
DECLARE curr_categoria VARCHAR(60);
BEGIN
    curr_categoria = NEW.super_categoria;
    WHILE curr_categoria != '' LOOP
        curr_categoria = (SELECT super_categoria FROM tem_outra 
                            WHERE categoria = curr_categoria);

        IF curr_categoria = NEW.categoria THEN
            RAISE EXCEPTION 'Uma Categoria não pode estar contida em si '
            'própria';
        END IF;
    END LOOP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_contida_em_loop
BEFORE UPDATE OR INSERT ON tem_outra
FOR EACH ROW EXECUTE PROCEDURE trg_contida_em_loop();

--------------------------------------------------------------------------------
-- (RI-2) O número de unidades repostas num Evento de Reposição não pode exceder
-- o número de unidades especificado no Planograma
--------------------------------------------------------------------------------

DROP TRIGGER IF EXISTS trg_evento_reposicao_unidades ON evento_reposicao;

CREATE OR REPLACE FUNCTION trg_evento_reposicao_unidades()
RETURNS TRIGGER AS
$$
BEGIN
    IF NEW.unidades > (SELECT unidades FROM planograma 
        WHERE (
            ean = NEW.ean AND nro = NEW.nro AND num_serie = NEW.num_serie AND
            fabricante = NEW.fabricante
        )) THEN
        RAISE EXCEPTION 'O número de unidades repostas num Evento de Reposição '
        'não pode exceder o número de unidades especificado no PlanogramaUma '
        'Categoria não pode estar contida em si própria';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_evento_reposicao_unidades
BEFORE UPDATE OR INSERT ON evento_reposicao
FOR EACH ROW EXECUTE PROCEDURE trg_evento_reposicao_unidades();

--------------------------------------------------------------------------------
-- (RI-3) Um Produto só pode ser reposto numa Prateleira que apresente 
-- (pelo menos) uma das Categorias desse produto
--------------------------------------------------------------------------------

DROP TRIGGER IF EXISTS trg_evento_reposicao_categoria ON evento_reposicao;

CREATE OR REPLACE FUNCTION trg_evento_reposicao_categoria()
RETURNS TRIGGER AS
$$
DECLARE ctg_da_prateleira VARCHAR(60);
BEGIN

    ctg_da_prateleira = (SELECT nome FROM prateleira 
        WHERE nro = NEW.nro AND num_serie = NEW.num_serie 
            AND fabricante = NEW.fabricante);
        
    IF ctg_da_prateleira NOT IN (
            SELECT nome FROM tem_categoria WHERE ean = NEW.ean
        ) THEN
        RAISE EXCEPTION '(RI-3) Um Produto só pode ser reposto numa Prateleira '
        'que apresente(pelo menos) uma das Categorias desse produto';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_evento_reposicao_categoria
BEFORE UPDATE OR INSERT ON evento_reposicao
FOR EACH ROW EXECUTE PROCEDURE trg_evento_reposicao_categoria();