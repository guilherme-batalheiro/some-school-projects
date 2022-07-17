#!/usr/bin/python3
from wsgiref.handlers import CGIHandler
from flask import Flask
from flask import render_template, request, redirect, url_for
import psycopg2
import psycopg2.extras

## SGBD configs
DB_HOST = "db.tecnico.ulisboa.pt"
DB_USER = "ist199075"
DB_DATABASE = DB_USER
DB_PASSWORD = "Rodinhas1"
DB_CONNECTION_STRING = "host=%s dbname=%s user=%s password=%s" % (
    DB_HOST,
    DB_DATABASE,
    DB_USER,
    DB_PASSWORD,
)

app = Flask(__name__, template_folder="templates")

@app.route("/")
def index():
    try:
        return render_template("index.html")
    except Exception as e:
        return str(e)


@app.route("/categorias")
def categorias():
    try:
        return render_template("categorias.html")
    except Exception as e:
        return str(e)

@app.route("/categorias/inserir_categoria")
def inserir_categoria():
    try:
        return render_template("inserir_categoria.html")
    except Exception as e:
        return str(e)

@app.route("/categorias/insercao_de_categoria", methods=["POST"])
def insercao_de_categoria():
    dbConn=None
    cursor=None
    try:
        dbConn = psycopg2.connect(DB_CONNECTION_STRING)
        cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)

        categorias = request.form.getlist("nome_da_categoria")
        if not all(categorias):
            raise Exception("Não podem haver categorias com o nome vazio")

        if(len(categorias) > 1):
            for nome in categorias:
                query = "INSERT INTO categoria VALUES (%s);"
                data=(nome,)
                cursor.execute(query,data)
        else:
            query = "INSERT INTO categoria VALUES (%s);"
            data=(categorias[0],)
            cursor.execute(query,data)
        
        if(len(categorias) > 1):
            for nome in categorias[:-1]:
                query = "INSERT INTO super_categoria VALUES (%s);"
                data=(nome,)
                cursor.execute(query,data)
        
            query = "INSERT INTO categoria_simples VALUES (%s);"
            data=(categorias[-1],)
            cursor.execute(query,data)
        else:
            query = "INSERT INTO categoria_simples VALUES (%s);"
            data=(categorias[0],)
            cursor.execute(query,data)

        if(len(categorias) > 1):
            for i in range(len(categorias[:-1])):
                query = "INSERT INTO tem_outra VALUES (%s, %s);"
                data=(categorias[i], categorias[i + 1])
                cursor.execute(query,data)

        pai_da_categoria=request.form["pai_da_categoria"]
        if pai_da_categoria != "":
            query = "INSERT INTO tem_outra VALUES (%s, %s);"
            data=(pai_da_categoria, categorias[0])
            cursor.execute(query,data)
        
        return redirect(url_for('categorias'))
    except Exception as e:
        return str(e)
    finally:
        dbConn.commit()
        cursor.close()
        dbConn.close()

@app.route("/categorias/remover_categoria")
def remover_categoria():
    try:
        return render_template("remover_categoria.html")
    except Exception as e:
        return str(e)

@app.route("/categorias/remocao_de_categoria", methods=["POST"])
def remocao_de_categoria():
    dbConn=None
    cursor=None
    try:
        dbConn = psycopg2.connect(DB_CONNECTION_STRING)
        cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
        
        nome_da_categoria=request.form["nome_da_categoria"]
        query = "DELETE FROM categoria WHERE nome=%s;"
        data=(nome_da_categoria,)
        cursor.execute(query,data)

        return redirect(url_for('categorias'))
    except Exception as e:
        return str(e)
    finally:
        dbConn.commit()
        cursor.close()
        dbConn.close()

@app.route("/categorias/listar_sub_categorias")
def listar_sub_categorias():
    try:
        return render_template("listar_sub_categorias.html")
    except Exception as e:
        return str(e)

        
@app.route("/categorias/executar_listar_categorias", methods=["POST"])
def executar_listar_categorias():
    dbConn=None
    cursor=None
    def obter_todas_as_sub_categorias(nome_da_categoria, sub_categorias):
        query = "SELECT categoria FROM tem_outra WHERE super_categoria=%s;"
        data=(nome_da_categoria,)
        cursor.execute(query,data)
        categorias = cursor.fetchall()
        for nome in categorias:
            sub_categorias[nome[0]] = {}
            sub_categorias[nome[0]] = obter_todas_as_sub_categorias(nome[0], sub_categorias[nome[0]])
        return sub_categorias

    def make_string(sub_categorias, p):
        string = "<body>"
        for name in sub_categorias.keys():
            string += p * "----" + name + "<br>" 
            if ((sub_categorias[name].keys()) != 0):
                string += make_string(sub_categorias[name], p + 1)

        return string +"</body>"

    try:
        dbConn = psycopg2.connect(DB_CONNECTION_STRING)
        cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
        
        query = "SELECT categoria FROM categoria WHERE nome=%s;"
        nome_da_categoria=request.form["nome_da_categoria"]
        data=(nome_da_categoria,)
        cursor.execute(query,data)
        categorias = cursor.fetchall()
        
        if(len(categorias) == 0):
            return "Categoria não existe" 

        sub_categorias = {nome_da_categoria: {}}        
        sub_categorias[nome_da_categoria] = obter_todas_as_sub_categorias(nome_da_categoria, sub_categorias[nome_da_categoria])

        return make_string(sub_categorias, 0)
    except Exception as e:
        return str(e)
    finally:
        dbConn.commit()
        cursor.close()
        dbConn.close()

@app.route("/retalhistas")
def retalhistas():
    try:
        return render_template("retalhistas.html")
    except Exception as e:
        return str(e)
        
@app.route("/retalhistas/inserir_retalhista")
def inserir_retalhista():
    try:
        return render_template("inserir_retalhista.html")
    except Exception as e:
        return str(e)
        
@app.route("/retalhistas/executar_insercao_retalhista", methods=["POST"])
def executar_insercao_retalhista():
    dbConn=None
    cursor=None
    try:
        dbConn = psycopg2.connect(DB_CONNECTION_STRING)
        cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
        
        nome_do_retalhista=request.form["nome_do_retalhista"]
        tin_do_retalhista=request.form["tin_do_retalhista"]
        query = "INSERT INTO retalhista VALUES (%s, %s);"
        data=(tin_do_retalhista, nome_do_retalhista)
        cursor.execute(query,data)

        return redirect(url_for('retalhistas'))
    except Exception as e:
        return str(e)
    finally:
        dbConn.commit()
        cursor.close()
        dbConn.close()
        
@app.route("/retalhistas/remover_retalhista")
def remover_retalhista():
    try:
        return render_template("remover_retalhista.html")
    except Exception as e:
        return str(e)
        
@app.route("/retalhistas/executar_remocao_retalhista", methods=["POST"])
def executar_remocao_retalhista():
    dbConn=None
    cursor=None
    try:
        dbConn = psycopg2.connect(DB_CONNECTION_STRING)
        cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
        
        tin_do_retalhista=request.form["tin_do_retalhista"]
        query_responsabilidades = "DELETE FROM responsavel_por WHERE tin = %s;"
        query_retalhista = "DELETE FROM retalhista WHERE tin = %s;"
        data=(tin_do_retalhista)
        cursor.execute(query_responsabilidades,data)
        cursor.execute(query_retalhista,data)

        return redirect(url_for('retalhistas'))
    except Exception as e:
        return str(e)
    finally:
        dbConn.commit()
        cursor.close()
        dbConn.close()
        
@app.route("/consultar_eventos")
def consultar_eventos():
    try:
        return render_template("consultar_eventos.html")
    except Exception as e:
        return str(e)
        
@app.route("/executar_consulta_eventos", methods=["POST"])
def executar_consulta_eventos():
    dbConn=None
    cursor=None
    try:
        dbConn = psycopg2.connect(DB_CONNECTION_STRING)
        cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
        
        fabricante=request.form["fabricante"]
        num_serie=request.form["num_serie"]
        query = "SELECT * FROM evento_reposicao WHERE num_serie = %s AND fabricante = %s;"
        data =(num_serie,fabricante)
        cursor.execute(query,data)
        eventos = cursor.fetchall()
        query_categorias = "SELECT nome AS categoria, SUM(unidades) AS unidades FROM evento_reposicao NATURAL JOIN tem_categoria WHERE num_serie = %s AND fabricante = %s GROUP BY nome;"
        cursor.execute(query_categorias, data)
        
        return render_template("resultado_eventos.html", num_serie=num_serie, fabricante=fabricante, eventos=eventos, cursor=cursor)
    except Exception as e:
        return str(e)
    finally:
        dbConn.commit()
        cursor.close()
        dbConn.close()

CGIHandler().run(app)
