DROP TABLE IF EXISTS categoria CASCADE;
DROP TABLE IF EXISTS categoria_simples CASCADE;
DROP TABLE IF EXISTS super_categoria CASCADE;
DROP TABLE IF EXISTS tem_outra CASCADE;
DROP TABLE IF EXISTS produto CASCADE;
DROP TABLE IF EXISTS tem_categoria CASCADE;
DROP TABLE IF EXISTS ivm CASCADE;
DROP TABLE IF EXISTS ponto_de_retalho CASCADE;
DROP TABLE IF EXISTS instalada_em CASCADE;
DROP TABLE IF EXISTS prateleira CASCADE;
DROP TABLE IF EXISTS planograma CASCADE;
DROP TABLE IF EXISTS retalhista CASCADE;
DROP TABLE IF EXISTS responsavel_por CASCADE;
DROP TABLE IF EXISTS evento_reposicao CASCADE;

CREATE TABLE categoria (
    nome VARCHAR(60) NOT NULL UNIQUE,
    CONSTRAINT pk_categoria PRIMARY KEY(nome)
);

CREATE TABLE categoria_simples (
    nome VARCHAR(60) NOT NULL UNIQUE,
    CONSTRAINT fk_categoria_simples_nome FOREIGN KEY(nome) 
        REFERENCES categoria(nome)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT pk_categoria_simples PRIMARY KEY(nome)
);

CREATE TABLE super_categoria (
    nome VARCHAR(60) NOT NULL UNIQUE,
    CONSTRAINT fk_super_categoria_nome FOREIGN KEY(nome) 
        REFERENCES categoria(nome)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT pk_super_categoria PRIMARY KEY(nome)
);

CREATE TABLE tem_outra (
    super_categoria VARCHAR(60) NOT NULL,
    categoria       VARCHAR(60) NOT NULL UNIQUE,
    CONSTRAINT ch_tem_outra_diff_sup_cat CHECK(super_categoria != categoria),
    CONSTRAINT fk_tem_outra_sup_cat_nome FOREIGN KEY(super_categoria)
        REFERENCES super_categoria(nome)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_tem_outra_cat_nome FOREIGN KEY(categoria) 
        REFERENCES categoria(nome)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT pk_tem_outra PRIMARY KEY(categoria)
);

CREATE TABLE produto (
    ean   CHAR(13) NOT NULL UNIQUE,
    cat   VARCHAR(60) NOT NULL,
    descr VARCHAR(90) NOT NULL,
    CONSTRAINT fk_produto FOREIGN KEY(cat) 
        REFERENCES categoria(nome)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT pk_produto PRIMARY KEY(ean)
);

CREATE TABLE tem_categoria (
    ean  CHAR(13) NOT NULL,
    nome VARCHAR(60) NOT NULL,
    CONSTRAINT fk_tem_categoria_ean FOREIGN KEY(ean) 
        REFERENCES produto(ean)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_tem_categoria_nome FOREIGN KEY(nome) 
        REFERENCES categoria(nome)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT pk_tem_categoria PRIMARY KEY(ean, nome)
);

CREATE TABLE ivm (
    num_serie  NUMERIC(12) NOT NULL,
    fabricante VARCHAR(60) NOT NULL,
    CONSTRAINT pk_ivm PRIMARY KEY(num_serie, fabricante)
);

CREATE TABLE ponto_de_retalho (
    nome     VARCHAR(60) NOT NULL UNIQUE,
    distrito VARCHAR(60) NOT NULL,
    concelho VARCHAR(60) NOT NULL,
    CONSTRAINT pk_ponto_de_retalho PRIMARY KEY(nome)
);

CREATE TABLE instalada_em (
    num_serie  NUMERIC(12) NOT NULL,
    fabricante VARCHAR(60) NOT NULL,
    local      VARCHAR(60) NOT NULL,
    CONSTRAINT fk_instalada_em_ivm FOREIGN KEY(num_serie, fabricante)
        REFERENCES ivm(num_serie, fabricante)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_instalada_em_local FOREIGN KEY(local)
        REFERENCES ponto_de_retalho(nome)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT pk_instalada_em PRIMARY KEY(num_serie, fabricante)
);

CREATE TABLE prateleira (
    nro        NUMERIC(10) NOT NULL,
    num_serie  NUMERIC(12) NOT NULL,
    fabricante VARCHAR(60) NOT NULL,
    altura     VARCHAR(60) NOT NULL,
    nome       VARCHAR(60) NOT NULL,
    CONSTRAINT fk_prateleira_ivm FOREIGN KEY(num_serie, fabricante) 
        REFERENCES ivm(num_serie, fabricante)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_prateleira_local FOREIGN KEY(nome) 
        REFERENCES categoria(nome)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT pk_prateleira PRIMARY KEY(nro, num_serie, fabricante)
);

CREATE TABLE planograma (
    ean        CHAR(13) NOT NULL,
    nro        NUMERIC(10) NOT NULL,
    num_serie  NUMERIC(12) NOT NULL,
    fabricante VARCHAR(60) NOT NULL,
    faces      NUMERIC(20) NOT NULL,
    unidades   NUMERIC(30) NOT NULL,
    loc        VARCHAR(20) NOT NULL,
    CONSTRAINT fk_planograma_ean FOREIGN KEY(ean) 
        REFERENCES produto(ean)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_planograma_prateleira FOREIGN KEY(nro, num_serie, fabricante)
        REFERENCES prateleira(nro, num_serie, fabricante)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT pk_planograma PRIMARY KEY(ean, nro, num_serie, fabricante)
);

CREATE TABLE retalhista (
    tin  NUMERIC(10) NOT NULL UNIQUE,
    nome VARCHAR(60) NOT NULL UNIQUE,
    CONSTRAINT pk_retalhista PRIMARY KEY(tin)
);

CREATE TABLE responsavel_por (
    nome_cat   VARCHAR(60) NOT NULL,
    tin        NUMERIC(10) NOT NULL,
    num_serie  NUMERIC(12) NOT NULL,
    fabricante VARCHAR(20) NOT NULL,
    CONSTRAINT fk_responsavel_por_tin FOREIGN KEY(tin) 
        REFERENCES retalhista(tin)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_responsavel_por_nome_cat FOREIGN KEY(nome_cat) 
        REFERENCES categoria(nome)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_responsavel_por_ivm FOREIGN KEY(num_serie, fabricante)
        REFERENCES ivm(num_serie, fabricante)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT pk_responsavel_por PRIMARY KEY(num_serie, fabricante)
);

CREATE TABLE evento_reposicao (
    ean        CHAR(13) NOT NULL,
    nro        NUMERIC(10) NOT NULL,
    num_serie  NUMERIC(12) NOT NULL,
    fabricante VARCHAR(60) NOT NULL,
    instante   TIMESTAMP NOT NULL CHECK(instante <= CURRENT_TIMESTAMP),
    unidades   NUMERIC(8) NOT NULL,
    tin        NUMERIC(10) NOT NULL,
    CONSTRAINT fk_evento_reposicao_planograma 
        FOREIGN KEY(ean, nro, num_serie, fabricante)
        REFERENCES planograma(ean, nro, num_serie, fabricante)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_evento_reposicao_tin FOREIGN KEY(tin) 
        REFERENCES retalhista(tin)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT pk_evento_reposicao PRIMARY KEY(ean, nro, num_serie, fabricante,
    instante)
);

--------------------------------------------------------------------------------
-- Populate Relations 
----------------------------------------

insert into categoria values('Snacks');
insert into categoria values('Barras');
insert into categoria values('Barras Energéticas');
insert into categoria values('Barras de Ceriais');
insert into categoria values('Batatas Fritas');
insert into categoria values('Chocolates');
insert into categoria values('Sandes');

insert into super_categoria values('Snacks');
insert into super_categoria values('Barras');
insert into categoria_simples values('Barras Energéticas');
insert into categoria_simples values('Barras de Ceriais');
insert into categoria_simples values('Batatas Fritas');
insert into categoria_simples values('Chocolates');
insert into categoria_simples values('Sandes');

insert into tem_outra values('Snacks', 'Barras');
insert into tem_outra values('Barras', 'Barras Energéticas');
insert into tem_outra values('Barras', 'Barras de Ceriais');
insert into tem_outra values('Snacks', 'Batatas Fritas');
insert into tem_outra values('Snacks', 'Chocolates');
insert into tem_outra values('Snacks', 'Sandes');

insert into produto values('9231871282638', 'Barras Energéticas', 'Fitness nutrition');
insert into produto values('9923921724633', 'Barras de Ceriais', 'Chocapique');
insert into produto values('9238774327635', 'Barras de Ceriais', 'Golden Grahams');
insert into produto values('9654765427632', 'Batatas Fritas', 'Batata Frita Ondulada');
insert into produto values('9991293327633', 'Batatas Fritas', 'Batata Frita Forno Ondulada Original');
insert into produto values('9232381237636', 'Batatas Fritas', 'Batata Frita Ondulada Sabor Presunto');
insert into produto values('9238779238635', 'Chocolates', 'Twix');
insert into produto values('9923814327639', 'Chocolates', 'Kit-Kat');
insert into produto values('9277217372133', 'Chocolates', 'Lion');
insert into produto values('9231383128379', 'Chocolates', 'Mars');
insert into produto values('9217391273379', 'Sandes', 'Sandes de Atum');
insert into produto values('9232183721379', 'Sandes', 'Sandes de Frango');
insert into produto values('9928376428379', 'Sandes', 'Sandes de europeia');
insert into produto values('9928765218379', 'Sandes', 'Sandes mista');

insert into tem_categoria values('9231871282638', 'Snacks');
insert into tem_categoria values('9231871282638', 'Barras');
insert into tem_categoria values('9231871282638', 'Barras Energéticas');
insert into tem_categoria values('9923921724633', 'Snacks');
insert into tem_categoria values('9923921724633', 'Barras');
insert into tem_categoria values('9923921724633', 'Barras de Ceriais');
insert into tem_categoria values('9238774327635', 'Snacks');
insert into tem_categoria values('9238774327635', 'Barras');
insert into tem_categoria values('9238774327635', 'Barras de Ceriais');
insert into tem_categoria values('9654765427632', 'Snacks');
insert into tem_categoria values('9654765427632', 'Batatas Fritas');
insert into tem_categoria values('9991293327633', 'Snacks');
insert into tem_categoria values('9991293327633', 'Batatas Fritas');
insert into tem_categoria values('9232381237636', 'Snacks');
insert into tem_categoria values('9232381237636', 'Batatas Fritas');
insert into tem_categoria values('9238779238635', 'Snacks');
insert into tem_categoria values('9238779238635', 'Chocolates');
insert into tem_categoria values('9923814327639', 'Snacks');
insert into tem_categoria values('9923814327639', 'Chocolates');
insert into tem_categoria values('9277217372133', 'Snacks');
insert into tem_categoria values('9277217372133', 'Chocolates');
insert into tem_categoria values('9231383128379', 'Snacks');
insert into tem_categoria values('9231383128379', 'Chocolates');
insert into tem_categoria values('9217391273379', 'Snacks');
insert into tem_categoria values('9217391273379', 'Sandes');
insert into tem_categoria values('9232183721379', 'Snacks');
insert into tem_categoria values('9232183721379', 'Sandes');
insert into tem_categoria values('9928376428379', 'Snacks');
insert into tem_categoria values('9928376428379', 'Sandes');

insert into IVM values(1, 'Opeletic');
insert into IVM values(2, 'Hilfo');
insert into IVM values(3, 'Yogo');
insert into IVM values(4, 'Figos');
insert into IVM values(5, 'Figos');
insert into IVM values(6, 'Figos');
insert into IVM values(7, 'Figos');
insert into IVM values(8, 'Figos');
insert into IVM values(9, 'Figos');
insert into IVM values(4, 'Yogo');
insert into IVM values(5, 'Yogo');
insert into IVM values(6, 'Yogo');
insert into IVM values(7, 'Yogo');
insert into IVM values(8, 'Yogo');

insert into ponto_de_retalho values('TagusPark', 'Lisboa', 'Oeiras');
insert into ponto_de_retalho values('Nova', 'Lisboa', 'Cascais');
insert into ponto_de_retalho values('Alameda', 'Lisboa', 'Lisboa');

insert into instalada_em values(1, 'Opeletic', 'TagusPark');
insert into instalada_em values(2, 'Hilfo', 'Nova');
insert into instalada_em values(3, 'Yogo', 'Alameda');

insert into prateleira values(1, 1, 'Opeletic', 1, 'Chocolates');
insert into prateleira values(2, 1, 'Opeletic', 2, 'Sandes');
insert into prateleira values(3, 1, 'Opeletic', 3, 'Barras');
insert into prateleira values(1, 2, 'Hilfo', 1, 'Batatas Fritas');
insert into prateleira values(2, 2, 'Hilfo', 2, 'Barras de Ceriais');
insert into prateleira values(1, 3, 'Yogo', 1, 'Batatas Fritas');
insert into prateleira values(2, 3, 'Yogo', 2, 'Barras Energéticas');

insert into planograma values('9238779238635', 1, 1, 'Opeletic', 3, 16, 'asjdsj');
insert into planograma values('9923814327639', 1, 1, 'Opeletic', 3, 16, 'asjdsj');
insert into planograma values('9277217372133', 1, 1, 'Opeletic', 3, 16, 'asjdsj');
insert into planograma values('9231383128379', 1, 1, 'Opeletic', 3, 16, 'asjdsj');
insert into planograma values('9217391273379', 2, 1, 'Opeletic', 3, 16, 'asjdsj');
insert into planograma values('9232183721379', 2, 1, 'Opeletic', 3, 16, 'asjdsj');
insert into planograma values('9928376428379', 2, 1, 'Opeletic', 3, 16, 'asjdsj');
insert into planograma values('9231871282638', 3, 1, 'Opeletic', 3, 16, 'asjdsj');
insert into planograma values('9923921724633', 3, 1, 'Opeletic', 3, 16, 'asjdsj');
insert into planograma values('9238774327635', 3, 1, 'Opeletic', 3, 16, 'asjdsj');
insert into planograma values('9654765427632', 1, 2, 'Hilfo', 3, 16, 'asjdsj');
insert into planograma values('9991293327633', 1, 2, 'Hilfo', 3, 16, 'asjdsj');
insert into planograma values('9232381237636', 1, 2, 'Hilfo', 3, 16, 'asjdsj');
insert into planograma values('9923921724633', 2, 2, 'Hilfo', 3, 16, 'asjdsj');
insert into planograma values('9238774327635', 2, 2, 'Hilfo', 3, 16, 'asjdsj');
insert into planograma values('9231871282638', 2, 3, 'Yogo', 3, 16, 'asjdsj');
insert into planograma values('9654765427632', 1, 3, 'Yogo', 3, 16, 'asjdsj');
insert into planograma values('9991293327633', 1, 3, 'Yogo', 3, 16, 'asjdsj');
insert into planograma values('9232381237636', 1, 3, 'Yogo', 3, 16, 'asjdsj');

insert into retalhista values(1, 'Inês Garcia');
insert into retalhista values(2, 'João Custódio');
insert into retalhista values(3, 'Manuel Covas');

insert into responsavel_por values('Snacks', 3, 1, 'Opeletic');
insert into responsavel_por values('Batatas Fritas', 3, 4, 'Figos');
insert into responsavel_por values('Snacks', 3, 2, 'Hilfo');
insert into responsavel_por values('Snacks', 1, 3, 'Yogo');
insert into responsavel_por values('Batatas Fritas', 3, 5, 'Figos');
insert into responsavel_por values('Barras Energéticas', 3, 6, 'Figos');
insert into responsavel_por values('Barras de Ceriais', 3, 7, 'Figos');
insert into responsavel_por values('Chocolates', 3, 8, 'Figos');
insert into responsavel_por values('Sandes', 3, 9, 'Figos');
insert into responsavel_por values('Batatas Fritas', 2, 4, 'Yogo');
insert into responsavel_por values('Barras Energéticas', 2, 5, 'Yogo');
insert into responsavel_por values('Barras de Ceriais', 2, 6, 'Yogo');
insert into responsavel_por values('Chocolates', 2, 7, 'Yogo');
insert into responsavel_por values('Sandes', 2, 8, 'Yogo');

insert into evento_reposicao values('9238779238635', 1, 1, 'Opeletic', '2022-06-15', 16, 1);
insert into evento_reposicao values('9923814327639', 1, 1, 'Opeletic', '2022-03-23', 16, 1);
insert into evento_reposicao values('9277217372133', 1, 1, 'Opeletic', '2022-04-23', 16, 1);
insert into evento_reposicao values('9231383128379', 1, 1, 'Opeletic', '2022-02-23', 16, 1);
insert into evento_reposicao values('9217391273379', 2, 1, 'Opeletic', '2022-04-23', 16, 1);
insert into evento_reposicao values('9232183721379', 2, 1, 'Opeletic', '2022-05-23', 16, 1);
insert into evento_reposicao values('9928376428379', 2, 1, 'Opeletic', '2022-02-23', 16, 1);
insert into evento_reposicao values('9231871282638', 3, 1, 'Opeletic', '2022-03-23', 16, 1);
insert into evento_reposicao values('9923921724633', 3, 1, 'Opeletic', '2022-04-23', 16, 1);
insert into evento_reposicao values('9238774327635', 3, 1, 'Opeletic', '2022-06-15', 16, 1);
insert into evento_reposicao values('9654765427632', 1, 2, 'Hilfo', '2022-01-23', 16, 2);
insert into evento_reposicao values('9991293327633', 1, 2, 'Hilfo', '2022-02-23', 16, 2);
insert into evento_reposicao values('9232381237636', 1, 2, 'Hilfo', '2022-03-23', 16, 2);
insert into evento_reposicao values('9923921724633', 2, 2, 'Hilfo', '2022-04-23', 16, 2);
insert into evento_reposicao values('9238774327635', 2, 2, 'Hilfo', '2022-02-23', 16, 2);
insert into evento_reposicao values('9231871282638', 2, 3, 'Yogo', '2022-03-23', 16, 3);
insert into evento_reposicao values('9654765427632', 1, 3, 'Yogo', '2022-05-23', 16, 3);
insert into evento_reposicao values('9991293327633', 1, 3, 'Yogo', '2022-02-23', 16, 3);
insert into evento_reposicao values('9232381237636', 1, 3, 'Yogo', '2022-03-23', 16, 3);
insert into evento_reposicao values('9232381237636', 1, 3, 'Yogo', '2021-03-23', 16, 3);
insert into evento_reposicao values('9277217372133', 1, 1, 'Opeletic', '2021-04-23', 16, 1);