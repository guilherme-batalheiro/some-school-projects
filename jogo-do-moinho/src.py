"""
Projeto 2 - Jogo do Galo

Guilherme Batalheiro
ist-99075
"""

#TAD posicao -------------------------------------------------------------------
#Construtor

def cria_posicao(c, l):
	"""Cria posição.

	:param c: "a", "b" ou "c"
	:param l: "1", "2" ou "3"
	:return:  posição

	cria_posicao(c,l) recebe duas cadeias de carateres correspondente à coluna c
	e à linha l de uma posição e devolve um dicionário com as chaves "coluna" e
	"linha" com os seus respetivos valores.
	"""
	if c in ('a', 'b', 'c') and l in ('1', '2', '3'):
		return {"coluna": c, "linha": l}
	raise ValueError("cria_posicao: argumentos invalidos")

def cria_copia_posicao(p):
	"""Cria copia da posição.

	:param p: posição
	:return:  posição

	cria_copia_posicao(p) recebe uma posição e devolve uma cópia nova da posção.
	"""
	return {"coluna": p["coluna"], "linha": p["linha"]}

#Seletores
def obter_pos_c(p):
	"""Obtem a coluna.

	:param p: posição
	:return:  str

	obter_pos_c(p) devolve a componente coluna c da posição p.
	"""
	return p["coluna"]

def obter_pos_l(p):
	"""Obtem a linha.

	:param p: posição
	:return:  str

	obter_pos_c(p) devolve a componente coluna c da posição p.
	"""
	return p["linha"]

#Reconhecedor
def eh_posicao(arg):
	"""Reconhece posição

	:param arg: universal
	:return: 	bool

	eh_posicao(arg) devolve True caso o seu argumento seja um TAD posicao e 
	False caso contrário.	
	"""
	return isinstance(arg, dict) and \
	 	   len(arg) == 2 and \
	 	   "coluna" in arg and \
	 	   arg["coluna"] in ('a', 'b', 'c') and \
	 	   "linha" in arg and \
	 	   arg["linha"] in ('1', '2', '3')

#Teste
def posicoes_iguais(p1, p2):
	"""Testa a igualdade de duas posições

	:param p1: posição
	:param p2: posição
	:return:   bool

	posicoes_iguais(p1, p2) devolve True apenas se p1 e p2 são posições e são 
	iguais.	
	"""
	return eh_posicao(p1) and \
	 	   eh_posicao(p2) and \
	 	   obter_pos_c(p1) == obter_pos_c(p2) and \
	 	   obter_pos_l(p1) == obter_pos_l(p2)

#Transformador
def posicao_para_str(p):
	"""Posição em string

	:param p: posição
	:return:  str

	posicao_para_str(p) devolve a cadeia de caracteres 'cl' que representa o seu
	argumento, sendo os valores c e l as componentes coluna e linha de p.	
	"""
	return "{}{}".format(obter_pos_c(p), obter_pos_l(p))

#Funçoes de alto nivel
def obter_posicoes_adjacentes(p):
	"""Posições adjacentes

	:param p: posição
	:return:  tuplo

	obter posicoes adjacentes(p) devolve um tuplo com as posições adjacentes à 
	posição p de acordo com a ordem de leitura do tabuleiro.

	"""
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
	"""Cria peça

	:param s: str
	:return:  peca

	cria_peca(s) recebe uma cadeia de carateres correspondente ao identificador
	de um dos dois jogadores ('X' ou 'O') ou a uma peça livre (' ') e devolve a
	peça correspondente.
	"""
	if s in ('X', 'O', ' '):
	 	return {"valor": s}
	raise ValueError("cria_peca: argumento invalido")

def cria_copia_peca(j):
	"""Cria copia da peça

	:param j: peca
	:return:  peca

	cria copia peca(j) recebe uma peça e devolve uma cópia nova da peça.
	"""
	return {"valor": j["valor"]}

#Reconhecedor
def eh_peca(arg):
	"""Reconhece peça

	:param arg: universal
	:return:  	bool

	eh_peca(arg) devolve True caso o seu argumento seja um TAD peca e False caso
	contrário.
	"""
	return isinstance(arg, dict) and \
		   len(arg) == 1 and \
		   "valor" in arg and \
		   (arg["valor"] in ('X', 'O', ' '))

#Teste
def pecas_iguais(j1, j2):
	"""Testa a igualdade de duas posiças

	:param j1:  peca
	:param j2:  peca
	:return:  	bool

	pecas iguais(p1, p2) devolve True apenas se p1 e p2 são peças e são iguais.
	"""
	return eh_peca(j1) and \
		   eh_peca(j2) and \
		   j1["valor"] == j2["valor"]

#Transformador
def peca_para_str(j):
	"""Peça para string
	
	:param j:  peca
	:return:   str

	peca_para_str(j) devolve a cadeia de caracteres que representa o jogador dono
	da peça, isto é, '[X]', '[O]' ou '[ ]'.
	"""
	return "[{}]".format(j["valor"])

#Funçoes de alto nivel
def peca_para_inteiro(j):
	"""Peça para inteiro
	
	:param j:  peca
	:return:   int

	peca_para_inteiro(j) devolve um inteiro valor 1, -1 ou 0, dependendo se a peça
	é do jogador 'X', 'O' ou livre, respetivamente.
	"""
	return 1 if pecas_iguais(j, cria_peca("X")) else \
		   -1 if pecas_iguais(j, cria_peca("O")) else \
		   0

def inteiro_para_peca(i):
	"""Peça para inteiro
	
	:param i:  int
	:return:   peca

	inteiro_para_peca(j) devolve uma peça "X", "O", " " dependendo se o inteiro 
	é 1, -1, 0
	"""
	return cria_peca("X") if i == 1 else \
		   cria_peca("O") if i == -1 else \
		   cria_peca(" ")

#TAD tabuleiro------------------------------------------------------------------
#Construtor
def cria_tabuleiro():
	"""Cria um tabuleiro
	
	:return:   tabuleiro

	cria_tabuleiro() devolve um tabuleiro de jogo do moinho de 3x3 sem posições
	ocupadas por peças de jogador.
	"""
	return {posicao_para_str(i) : cria_peca(" ") for i in \
			 [cria_posicao(j,i) for i in ('1', '2', '3') for j in ('a', 'b', 'c')]}

def cria_copia_tabuleiro(t):
	"""Cria copia de um tabuleiro
	
	:param t:  tabuleiro
	:return:   tabuleiro

	cria_copia_tabuleiro(t) recebe um tabuleiro e devolve uma cópia nova do 
	tabuleiro.
	"""
	return {i : t[i] for i in t.keys()}

#Seletorets
def obter_peca(t, p):
	"""Obtem a peça de uma posição
	
	:param t:  tabuleiro
	:param p:  posicao
	:return:   peca

	obter_peca(t, p) devolve a peça na posição p do tabuleiro. Se a posição não
	estiver ocupada, devolve uma peça livre.
	"""
	return t[posicao_para_str(p)]

def obter_vetor(t, s):
	"""Obtem o vetor
	
	:param t:  tabuleiro
	:param s:  str
	:return:   peca

	obter_vetor(t, s) devolve todas as peças da linha ou coluna especificada pelo
	seu argumento.
	"""
	d = {"a": ("a1", "a2", "a3"),
		 "b": ("b1", "b2", "b3"),
		 "c": ("c1", "c2", "c3"),
		 "1": ("a1", "b1", "c1"),
		 "2": ("a2", "b2", "c2"),
		 "3": ("a3", "b3", "c3")}
	return (t[d[s][0]], t[d[s][1]], t[d[s][2]])

#Modificadores
def coloca_peca(t, j, p):
	"""Coloca peca no tabuleiro
	
	:param t:  tabuleiro
	:param j:  peca
	:param p:  posicao
	:return:   tabuleiro

	coloca peca(t, j, p) modifica destrutivamente o tabuleiro t colocando a peça
	j na posicão p, e devolve o próprio tabuleiro.
	"""
	t[posicao_para_str(p)] = j
	return t

def remove_peca(t, p):
	"""Remove peca no tabuleiro
	
	:param t:  tabuleiro
	:param p:  posicao
	:return:   tabuleiro

	remove_peca(t, p) modifica destrutivamente o tabuleiro t removendo a peça
	da posição p, e devolve o próprio tabuleiro.
	"""
	t[posicao_para_str(p)] = cria_peca(" ")
	return t

def move_peca(t, p1, p2):
	"""Movimenta peca no tabuleiro
	
	:param t:  tabuleiro
	:param p1: posicao
	:param p2: posicao
	:return:   tabuleiro

	move_peca(t, p1, p2) modifica destrutivamente o tabuleiro t movendo a peça
	que se encontra na posição p1 para a posição p2, e devolve o próprio tabuleiro.
	"""
	return remove_peca(coloca_peca(t, obter_peca(t, p1), p2), p1)

#Reconhecedor
def eh_tabuleiro(arg):
	"""Avalia se é tabuleiro
	
	:param arg: universal
	:return:  	bool

	eh_tabuleiro(arg) devolve True caso o seu argumento seja um TAD tabuleiro
	e False caso contrário. Um tabuleiro válido pode ter um máximo de 3 peças
	de cada jogador, não pode conter mais de 1 peça mais de um jogador que do
	contrario, e apenas pode haver um ganhador em simultâneo.
	"""
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
	"""Avalia se é posicao livre
	
	:param t: 	tabuleiro
	:param p: 	posicao
	:return:  	bool

	eh_posicao_livre(t, p) devolve True apenas no caso da posição p do tabuleiro
	corresponder a uma posição livre.
	"""
	return pecas_iguais(obter_peca(t, p), cria_peca(" "))

#Teste
def tabuleiros_iguais(t1, t2):
	"""Testa a igualdade de dois tabuleiro

	:param t1:  tabuleiro
	:param t2:  tabuleiro
	:return:  	bool

	tabuleiros_iguais(t1, t2) devolve True apenas se t1 e t2 são tabuleiros e são
	iguais.
	"""
	return eh_tabuleiro(t1) and \
	 	   eh_tabuleiro(t2) and \
	 	   all([pecas_iguais(obter_peca(t1, i), obter_peca(t2, i)) for i in \
	 		   [cria_posicao(j,i) for i in "123" for j in "abc"]])

#Transformador
def tabuleiro_para_str(t):
	"""Tabuleiro para string

	:param t:   tabuleiro
	:return:  	str
	
	tabuleir_para_str(t) devolve a cadeia de caracteres que representa o tabuleiro
	como mostrado no exemplo a seguir.
	>>> t = cria_tabuleiro()
	>>> s = tabuleiro_para_str(coloca_peca(t, cria_peca('X'),cria_posicao('a','1')))
	>>> print(s)
	   a   b   c
	1 [X]-[ ]-[ ]
	   | \ | / |
	2 [ ]-[ ]-[ ]
	   | / | \ |
	3 [ ]-[ ]-[ ]
	"""
	return "   a   b   c\n1 "+ peca_para_str(t["a1"]) +"-"+ peca_para_str(t["b1"]) +"-"+ peca_para_str(t["c1"]) +"\n   | \\ | / |\n2 "+ peca_para_str(t["a2"]) +"-"+ peca_para_str(t["b2"]) +"-"+ peca_para_str(t["c2"]) +"\n   | / | \\ |\n3 "+ peca_para_str(t["a3"]) +"-"+ peca_para_str(t["b3"]) +"-"+ peca_para_str(t["c3"]) +""

def tuplo_para_tabuleiro(tpl):
	"""Tuplo para um tabuleiro

	:param tpl: tuplo
	:return:  	tabuleiro

	tuplo_para_tabuleiro(tpl) devolve o tabuleiro que é representado pelo tuplo 
	tpl com 3 tuplos, cada um deles contendo 3 valores inteiros iguais a 1, -1 ou 0
	que corresponde respetivamente ás peças "X", "O", " "
	"""
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
	"""Obtem o ganhador

	:param t:	tabuleiro
	:return:  	peca

	obter_ganhador(t) devolve uma peça do jogador que tenha as suas 3 peças em 
	linha na vertical ou na horizontal no tabuleiro. Se não existir nenhum 
	ganhador, devolve uma peça livre.
	"""
	for i in ["a", "b", "c", "1", "2", "3"]:
		tpl = obter_vetor(t, i)
		if pecas_iguais(tpl[0], tpl[1]) and \
		   pecas_iguais(tpl[0], tpl[2]) and \
		   not pecas_iguais(tpl[0], cria_peca(" ")):
		   return tpl[0]

	return cria_peca(" ")

def obter_posicoes_livres(t):
	"""Obtem as posições livres

	:param t:	tabuleiro
	:return:  	tuple de posicoes

	obter_posicoes_livres(t) devolve um tuplo com as posições não ocupadas pelas
	peças de qualquer um dos dois jogadores na ordem de leitura do tabuleiro.
	"""
	return tuple(i for i in [cria_posicao(j,i) for i in "123" for j in "abc"] if \
	 			 pecas_iguais(obter_peca(t, i), cria_peca(" ")))

def obter_posicoes_jogador(t, p):
	"""Obtem as posições de um jogador

	:param t:	tabuleiro
	:param p:   peca
	:return:  	tuple de posicoes

	obter_posicoes_livres(p) devolve um tuplo com as posições não ocupadas pelas
	peças de qualquer um dos dois jogadores na ordem de leitura do tabuleiro.
	"""
	return tuple(i for i in [cria_posicao(j,i) for i in "123" for j in "abc"] if \
	 			 pecas_iguais(obter_peca(t, i), p))

#-------------------------------------------------------------------------------
def obter_movimento_manual(tab, peca):
	"""Moviemnto manual

	:param tab:	 tabuleiro
	:param peca: peca
	:return:  	 tuple de posicoes

	Função auxiliar que recebe um tabuleiro e uma peca de um jogador, e devolve 
	um tuplo com uma ou duas posições que representam uma posiçãao ou um movimento
	introduzido	manualmente pelo jogador. Na fase de colocação, o tuplo contém 
	apenas a posição escolhida pelo utilizador onde colocar uma nova peça. 
	Na fase de movimento, o tuplo contém a posição de origem da peça que se deseja
	movimentar e a posição de destino. Se não for possível movimentar nenhuma peça
	por estarem todas bloqueadas, o jogador pode passar turno escolhendo como movimento
	a posição duma peça própria seguida da mesma posição que ocupa.
	"""
	#vê se é para escolher uma posição ou um movimento
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
	"""Movimento auto

	:param tab:	  tabuleiro
	:param peca:  peca
	:param str:   string
	:return:  	  tuple de posicoes

	Função auxiliar que recebe um tabuleiro, uma peca de um jogador e uma cadeia de
	carateres representando o nível de dificuldade do jogo, e devolve um tuplo com uma ou
	duas posições que representam uma posição ou um movimento escolhido automaticamente.
	Na fase de colocação, o tuplo contém apenas a posição escolhida automaticamente
	onde colocar uma nova peçaa seguindo as regras da secção. Se não for possível
	movimentar nenhuma peça por estarem todas bloqueadas, a função devolve como movimento
	a posição da primeira peça do jogador correspondente seguida da mesma posição
	que ocupa. Na fase de movimento, o tuplo contém a posição de origem da peça movimentar
	e a posição de destino. A escolha automaticamente do movimento depende do nível
	de dificuldade do jogo.
	"""
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
	"""
    Funcao principal do jogo do moinho

    :param human: str
    :param estrategia: str
    :return: str

    Função principal que permite jogar um jogo completo do jogo do moinho de um jogador
	contra o computador. A função recebe duas cadeias de caracteres e devolve a 
	representação externa da peça ganhadora ('[X]' ou '[O]'). O primeiro argumento corresponde
	a representação externa da peça com que deseja jogar o jogador humano, e o segundo
	argumento selecciona o nível de dificuldade do jogo.
    """
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