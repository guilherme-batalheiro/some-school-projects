"""
Projeto 2 - Jogo do Galo

Guilherme Batalheiro
ist-99075
"""

#TAD posicao -------------------------------------------------------------------
#Construtor

def cria_posicao(c, l):
	if c in ('a', 'b', 'c') and l in ('1', '2', '3'):
		return {"coluna": c, "linha": l}
	raise ValueError("cria_posicao: argumentos invalidos")

def cria_copia_posicao(p):
	return {"coluna": p["coluna"], "linha": p["linha"]}

#Seletores
def obter_pos_c(p):
	return p["coluna"]

def obter_pos_l(p):
	return p["linha"]

#Reconhecedor
def eh_posicao(arg):
	return isinstance(arg, dict) and \
	 	   len(arg) == 2 and \
	 	   "coluna" in arg and \
	 	   arg["coluna"] in ('a', 'b', 'c') and \
	 	   "linha" in arg and \
	 	   arg["linha"] in ('1', '2', '3')

#Teste
def posicoes_iguais(p1, p2):
	return eh_posicao(p1) and \
	 	   eh_posicao(p2) and \
	 	   obter_pos_c(p1) == obter_pos_c(p2) and \
	 	   obter_pos_l(p1) == obter_pos_l(p2)

#Transformador
def posicao_para_str(p):
	return "{}{}".format(obter_pos_c(p), obter_pos_l(p))

#Funçoes de alto nivel
def obter_posicoes_adjacentes(p):
	return tuple(i for i in \
				 [cria_posicao(j,i) for i in "123" for j in "abc"] \
				 if 2 != abs(ord(obter_pos_c(p)) - ord(obter_pos_c(i))) != \
				 abs(ord(obter_pos_l(p)) - ord(obter_pos_l(i))) != 2 or \
				 (obter_pos_c(p) != "b" and obter_pos_l(p) != "2" and \
				 obter_pos_c(i) == "b" and  obter_pos_l(i) == "2") or \
				 (obter_pos_c(p) == "b" and obter_pos_l(p) == "2" and \
				 obter_pos_c(i) != "b" and  obter_pos_l(i) != "2"))

#TAD peca-----------------------------------------------------------------------
#Construtor
def cria_peca(s):
	if s in ('X', 'O', ' '):
	 	return {"valor": s}
	raise ValueError("cria_peca: argumento invalido")

def cria_copia_peca(j):
	return {"valor": j["valor"]}

#Reconhecedor
def eh_peca(arg):
	return isinstance(arg, dict) and \
		   len(arg) == 1 and \
		   "valor" in arg and \
		   (arg["valor"] in ('X', 'O', ' '))

#Teste
def pecas_iguais(j1, j2):
	return eh_peca(j1) and \
		   eh_peca(j2) and \
		   j1["valor"] == j2["valor"]

#Transformador
def peca_para_str(j):
	return "[{}]".format(j["valor"])

#Funçoes de alto nivel
def peca_para_inteiro(j):
	return 1 if pecas_iguais(j, cria_peca("X")) else \
		   -1 if pecas_iguais(j, cria_peca("O")) else \
		   0

def inteiro_para_peca(i):
	return cria_peca("X") if i == 1 else \
		   cria_peca("O") if i == -1 else \
		   cria_peca(" ")

#TAD tabuleiro------------------------------------------------------------------
#Construtor
def cria_tabuleiro():
	return {posicao_para_str(i) : cria_peca(" ") for i in \
			 [cria_posicao(j,i) for i in ('1', '2', '3') for j in ('a', 'b', 'c')]}

def cria_copia_tabuleiro(t):
	return {i : t[i] for i in t.keys()}

#Seletorets
def obter_peca(t, p):
	return t[posicao_para_str(p)]

def obter_vetor(t, s):
	d = {"a": ("a1", "a2", "a3"),
		 "b": ("b1", "b2", "b3"),
		 "c": ("c1", "c2", "c3"),
		 "1": ("a1", "b1", "c1"),
		 "2": ("a2", "b2", "c2"),
		 "3": ("a3", "b3", "c3")}
	return (t[d[s][0]], t[d[s][1]], t[d[s][2]])

#Modificadores
def coloca_peca(t, j, p):
	t[posicao_para_str(p)] = j
	return t

def remove_peca(t, p):
	t[posicao_para_str(p)] = cria_peca(" ")
	return t

def move_peca(t, p1, p2):
	return coloca_peca(remove_peca(t, p1), obter_peca(t, p1), p1)

#Reconhecedor
def eh_tabuleiro(arg):
	#Avalia a estrutura do tabuleiro.
	if not(isinstance(arg, dict) and \
		   len(arg) == 9 and \
		   all([True if (eh_peca(arg[i]) and \
						 eh_posicao(cria_posicao(i[0], i[1]))) \
						 else False for i in arg.keys()])):
		return False

	#Avalia o numero das peças.
	x = 0
	o = 0
	for i in arg.keys():
		if peca_para_inteiro(arg[i]) == 1:
			x += 1
		elif peca_para_inteiro(arg[i]) == -1:
			o += 1
	if x > 3 or o > 3 or abs(x - o) > 1:
		return False

	
	#Avalia se so um jogador ganhou.
	w = 0
	for i in ('a', 'b', 'c', '1', '2', '3'):
		tpl = obter_vetor(arg, i)
		if pecas_iguais(tpl[0], tpl[1]) and \
		   pecas_iguais(tpl[0], tpl[2]) and \
		   not pecas_iguais(tpl[0], cria_peca(" ")):
			w += 1
		if w > 1:
			return False
	
	return True

def eh_posicao_livre(t, p):
	return pecas_iguais(obter_peca(t, p), cria_peca(" "))

#Teste
def tabuleiros_iguais(t1, t2):

	return eh_tabuleiro(t1) and \
	 	   eh_tabuleiro(t2) and \
	 	   all([pecas_iguais(obter_peca(t1, i), obter_peca(t2, i)) for i in \
	 		   [cria_posicao(j,i) for i in "123" for j in "abc"]])

#Transformador
def tabuleiro_para_str(t):
	return "   a   b   c\n1 "+ peca_para_str(t["a1"]) +"-"+ peca_para_str(t["b1"]) +"-"+ peca_para_str(t["c1"]) +"\n   | \\ | / |\n2 "+ peca_para_str(t["a2"]) +"-"+ peca_para_str(t["b2"]) +"-"+ peca_para_str(t["c2"]) +"\n   | / | \\ |\n3 "+ peca_para_str(t["a3"]) +"-"+ peca_para_str(t["b3"]) +"-"+ peca_para_str(t["c3"]) +""

def tuplo_para_tabuleiro(tpl):
	t = cria_tabuleiro()
	c = ('a', 'b', 'c')
	l = ('1', '2', '3')
	p_d = {1: "X", -1: "O", 0: " "}
	for i in range(3):
		for j in range(3):
			t = coloca_peca(t, cria_peca(p_d[tpl[i][j]]), cria_posicao(c[j], l[i]))
	return t

#Funçoes de alto nivel
def obter_ganhador(t):
	for i in ["a", "b", "c", "1", "2", "3"]:
		tpl = obter_vetor(t, i)
		if pecas_iguais(tpl[0], tpl[1]) and \
		   pecas_iguais(tpl[0], tpl[2]) and \
		   not pecas_iguais(tpl[0], cria_peca(" ")):
		   return tpl[0]

	return cria_peca(" ")

def obter_posicoes_livres(t):
	return tuple(i for i in [cria_posicao(j,i) for i in "123" for j in "abc"] if \
	 			 pecas_iguais(obter_peca(t, i), cria_peca(" ")))

def obter_posicoes_jogador(t, p):
	return tuple(i for i in [cria_posicao(j,i) for i in "123" for j in "abc"] if \
	 			 pecas_iguais(obter_peca(t, i), p))

#-------------------------------------------------------------------------------
def obter_movimento_manual(tab, peca):
	if len(obter_posicoes_livres(tab)) > 3:
		inp = input("Turno do jogador. Escolha uma posicao: ")
		if not(len(inp) == 2 and \
			   inp[0] in ('a', 'b', 'c') and \
			   inp[1] in ('1', '2', '3') and \
		 	   pecas_iguais(obter_peca(tab, cria_posicao(inp[0], inp[1])), cria_peca(" "))):
			raise ValueError("obter_movimento_manual: escolha invalida")
		
		return (cria_posicao(inp[0], inp[1]),)

	else:
		inp = input("Turno do jogador. Escolha um movimento: ")
		if len(inp) == 4 and \
		   (inp[0] and inp[2]) in ('a', 'b', 'c') and \
		   (inp[1] and inp[3]) in ('1', '2', '3') and \
		   pecas_iguais(obter_peca(tab, cria_posicao(inp[0], inp[1])), peca) and \
		   not eh_posicao_livre(tab, cria_posicao(inp[0], inp[1])) and \
		   ((eh_posicao_livre(tab, cria_posicao(inp[2], inp[3])) and \
		   cria_posicao(inp[2], inp[3]) in obter_posicoes_adjacentes(cria_posicao(inp[0], inp[1]))) or \
		   (posicoes_iguais(cria_posicao(inp[0], inp[1]), cria_posicao(inp[2], inp[3])) and \
		   all([True if not eh_posicao_livre(tab, j) else False for i in obter_posicoes_jogador(tab, peca) for j in obter_posicoes_adjacentes(i)]))):

			return (cria_posicao(inp[0], inp[1]), cria_posicao(inp[2], inp[3]))
		raise ValueError("obter_movimento_manual: escolha invalida")

def obter_movimento_auto(tab, peca, str):
	def minimax(tab, jog, prof, seq_movi):
		if obter_ganhador(tab) != cria_peca(" "):
			return peca_para_inteiro(obter_ganhador(tab)), seq_movi
		elif prof == 0:
			return 0, seq_movi
		else:
			m_resul = -peca_para_inteiro(jog)
			m_seq_movi = ()
			for posi in obter_posicoes_jogador(tab, jog):
				for posi_adj in obter_posicoes_adjacentes(posi):
					if eh_posicao_livre(tab, posi_adj):
						tab_c = cria_copia_tabuleiro(tab)
						n_resul, n_seq_movi = minimax(move_peca(tab_c, posi, posi_adj), inteiro_para_peca(-peca_para_inteiro(jog)), prof - 1, seq_movi + (posi, posi_adj))
						if not(m_seq_movi) or \
							  (jog == cria_peca("X") and n_resul > m_resul) or \
							  (jog == cria_peca("O") and n_resul < m_resul):
							m_resul, m_seq_movi = n_resul, n_seq_movi 

			return m_resul, m_seq_movi

	def colocacao(tab, peca):
		def get_win_moves(tab, peca_int):
			livre = obter_posicoes_livres(tab)
			return tuple(posi for posi in livre if \
						 peca_para_inteiro(obter_ganhador(coloca_peca(cria_copia_tabuleiro(tab), inteiro_para_peca(peca_int), posi))) == peca_int)

		def rule1(tab, peca):   # win rule
			return get_win_moves(tab, peca_para_inteiro(peca))

		def rule2(tab, peca):	# block rule
			return get_win_moves(tab, -peca_para_inteiro(peca))

		def rule3(tab, peca):	# center rule
			return (cria_posicao("b", "2"),) if eh_posicao_livre(tab, cria_posicao("b", "2")) else ()

		def rule4(tab, peca):	# corner rule
			corners = (cria_posicao("a", "1"), cria_posicao("c", "1"), cria_posicao("a", "3"), cria_posicao("c", "3"))
			return tuple(posi for posi in corners if eh_posicao_livre(tab, posi))

		def rule5(tab, peca):	# side rule
			sides = (cria_posicao("b", "1"), cria_posicao("a", "2"), cria_posicao("c", "2"), cria_posicao("b", "3"))
			return tuple(posi for posi in sides if eh_posicao_livre(tab, posi))

		rules = (rule1, rule2, rule3, rule4, rule5)
		for rule in rules:
			candidate = rule(tab, peca)
			if candidate:
				return (candidate[0],)

	if len(obter_posicoes_livres(tab)) > 3:
		return colocacao(tab, peca)
	elif str == "facil":
		for i in obter_posicoes_jogador(tab, peca):
			for j in obter_posicoes_adjacentes(i):
				if pecas_iguais(obter_peca(tab, j), cria_peca(" ")):
					return (i, j)

		return (obter_posicoes_jogador(tab, peca)[0], obter_posicoes_jogador(tab, peca)[0])
	else:
		n = {"normal": 1, "dificil": 5}
		m_resul, m_seq_movi = minimax(tab, peca, n[str], ())
		if(m_seq_movi):
			return (m_seq_movi[0], m_seq_movi[1])
		return (obter_posicoes_jogador(tab, peca)[0], obter_posicoes_jogador(tab, peca)[0])

def moinho(human, estrategia):
	if not(human in ('[X]', '[O]') and estrategia in ('facil', 'normal', 'dificil')):
		raise ValueError('moinho: argumentos invalidos')

	tab = cria_tabuleiro()
	current_player = cria_peca("X")
	print('Bem-vindo ao JOGO DO MOINHO. Nivel de dificuldade '+ estrategia +'.', tabuleiro_para_str(tab), sep='\n')
	human = cria_peca("X") if human == '[X]' else cria_peca("O")

	while obter_ganhador(tab) == cria_peca(" "):
		if human == current_player:
			p = obter_movimento_manual(tab, current_player)
		else:
			print('Turno do computador (', estrategia, '):', sep='')
			p = obter_movimento_auto(tab,  current_player, estrategia)

		if len(obter_posicoes_livres(tab)) > 3:
			coloca_peca(tab, current_player, cria_copia_posicao(p[0]))
		else:
			if p[0] != p[1]:
				move_peca(tab, p[0], p[1])
		print(tabuleiro_para_str(tab))
		current_player = inteiro_para_peca(-peca_para_inteiro(current_player))

	if obter_ganhador(tab) == cria_peca("X"):
		return '[X]'
	else:
		return '[O]'