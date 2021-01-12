#Guilherme Batalheiro IST-199075
def eh_tabuleiro(tab):
	if not(type(tab) is tuple) or len(tab) != 3:								#avalia se tab é um tuplo de tamanho 3
		return False

	for i in range(3):  														#avalia cada tuplo
		if not(type(tab[i]) is tuple) or len(tab[i]) != 3:
			return False
		for j in range(3):														
			if not(type(tab[i][j]) is int) or tab[i][j] not in (1, 0, -1):		#avalia se é cada tuplo é composto por um valor -1, 1 ou 0
				return False
	
	return True

def eh_posicao(p):
	if not(type(p) is int) or not(0 < p < 10):
		return False
	
	return True

def obter_coluna(tab, n):
	if not eh_tabuleiro(tab) or not(type(n) is int) or not(0 < n < 4):
		raise ValueError("obter_coluna: algum dos argumentos e invalido")
	
	return (tab[0][n - 1], tab[1][n - 1], tab[2][n - 1])

def obter_linha(tab, n):
	if not eh_tabuleiro(tab) or not(type(n) is int) or not(0 < n < 4):
		raise ValueError("obter_linha: algum dos argumentos e invalido")
	
	return (tab[n - 1][0], tab[n - 1][1], tab[n - 1][2])

def obter_diagonal(tab, n):
	if not eh_tabuleiro(tab) or not(type(n) is int) or n not in (1, 2):
		raise ValueError("obter_diagonal: algum dos argumentos e invalido")
	if n == 1:
		return (tab[0][0], tab[1][1], tab[2][2])
	else:
		return (tab[2][0], tab[1][1], tab[0][2])

def tabuleiro_str(tab):
	if not eh_tabuleiro(tab):
		raise ValueError("tabuleiro_str: o argumento e invalido")

	st = ''
	for i in range(3):
		for j in range(3):
			if tab[i][j] == 1:
				st += " X "
			elif tab[i][j] == -1:
				st += " O "
			else:
				st += "   "

			if j != 2:															#ve se e o ultimo simbolo da linha
				st += "|"
		if i != 2:
			st += "\n-----------\n"

	return st

def eh_posicao_livre(tab, p):
	if not eh_tabuleiro(tab) or not eh_posicao(p):
		raise ValueError("eh_posicao_livre: algum dos argumentos e invalido")

	i = p // 3 + (p % 3 != 0) - 1												#calcula o numero da linha de p
	if tab[i][p - i * 3 - 1] == 0:												#p - i * 3 - 1 calcula o nurmero da coluna de p
		return True
	else:
		return False

def obter_posicoes_livres(tab):
	if not eh_tabuleiro(tab):
		raise ValueError("obter_posicoes_livres: o argumento e invalido")
	
	return tuple(i for i in range(1, 10) if eh_posicao_livre(tab, i))

def jogador_ganhador(tab):
	def same_number(tpl):
		if tpl[0] == tpl[1] == tpl[2] != 0:
			return True
		else:
			return False 

	if not eh_tabuleiro(tab):
		raise ValueError("jogador_ganhador: o argumento e invalido")

	for i in range(3):															
		if same_number(obter_coluna(tab, i + 1)):								#vai ver todas as colunas
			return tab[0][i]

	for i in range(3):
		if same_number(obter_linha(tab, i + 1)):								#vai ver todas as linhas
			return tab[i][1]

	for i in range(2):
		if same_number(obter_diagonal(tab, i + 1)):								#vai ver as duas diagonais
			return tab[1][1]

	return 0

def marcar_posicao(tab, n, p):
	if not eh_posicao(p) or not eh_tabuleiro(tab) or not eh_posicao_livre(tab, p)\
	   or not(type(n) is int)  or (n != 1 and n!= -1):
		raise ValueError("marcar_posicao: algum dos argumentos e invalido")

	lt = list(tab)																#transforma tab numa lista para ser possivel de alteral
	i = p // 3 + (p % 3 != 0) - 1												#calcula o numero da linha de p
	lt[i] = lt[i][:p - i * 3 - 1] + (n, ) + lt[i][p - i * 3:]					#corta um tuplo e acrescenta um valor
	return tuple(lt)

def escolher_posicao_manual(tab):
	if not eh_tabuleiro(tab):
		raise ValueError("escolher_posicao_manual: o argumento e invalido")

	p = int(input("Turno do jogador. Escolha uma posicao livre: "))
	if not eh_posicao(p) or not eh_posicao_livre(tab, p):
		raise ValueError("escolher_posicao_manual: a posicao introduzida e invalida")

	return p

def escolher_posicao_auto(tab, n, st):
	if not eh_tabuleiro(tab) or not(type(n) is int) or (n != 1 and n != -1) or\
	   not isinstance(st, str) or st not in ("basico", "normal", "perfeito"):
		raise ValueError("escolher_posicao_auto: algum dos argumentos e invalido")

	#1 e 2
	def estrategia_ver_se_falta_uma(tab, n, tpl):
		def same_number_except_one(tpl, n):											#vê se um tuplo têm doi numeros iguais menos um que tem de ser 0 ex:(1, 1, 0) == True | (1, -1, 0) == False
			if tpl[0] == tpl[1] == n and tpl[2] == 0 or\
				tpl[0] == tpl[2] == n and tpl[1] == 0 or\
				tpl[1] == tpl[2] == n and tpl[0] == 0:
				return True
			else:
				return False

		for i in tpl:																#vai a cada posição ver se e a que falata para ganhar
			l = i // 3 + (i % 3 != 0) - 1											#calcula o numero da linha de p
			c = i - l * 3 - 1														#calcula o numero da coluna de p
			
			if same_number_except_one(obter_linha(tab, l + 1), n):
				return i

			if same_number_except_one(obter_coluna(tab, c + 1), n):
				return i
					
			if i % 2 != 0:
				if i == 1 or i == 5 or i == 9:  
					if same_number_except_one(obter_diagonal(tab, 1), n):
						return i
				else:
					if same_number_except_one(obter_diagonal(tab, 2), n):
						return i

		return 0
	
	#3 e 4 
	#se for para atacar vai a procura das posicoes que tem bifurcacoes e devolve a primeira
	#se for para defender vaia procura das posicoes que tem bifurcacoes do inimigo guarda num tuplo e defende
	def estrategia_bifurcacao(tab, n, tpl, defender):
		def just_one_number(tpl, n):												#vê se um tuplo têm apenas um numero e os outros 0 ex:(1, 0, 0) == True | (1, -1, 0) == False
			if tpl[0] == tpl[1] == 0 and tpl[2] == n or\
				tpl[0] == tpl[2] == 0 and tpl[1] == n or\
				tpl[1] == tpl[2] == 0 and tpl[0] == n:
				return True
			else:
				return False

		if defender:																
			t = ()																	#cria um tuplo vazio onde vai guardar as posicoes que o inimigo pode escolher para fazer bifurcacao

		for i in tpl:																#ve quais as posicaoes possiveis para fazer bifurcacoes e guarda em t
			ha_diagonal = 0															
			ha_linha = 0
			ha_coluna = 0

			l = i // 3 + (i % 3 != 0) - 1 											#Calcula a linha da posiçao
			c = i - l * 3 - 1														#Calcula a coluna da posiçao

			ha_linha = just_one_number(obter_linha(tab, l + 1), n)					
			ha_coluna = just_one_number(obter_coluna(tab, c + 1), n)

			if i % 2 != 0:
				if i in (1, 5, 9):
					ha_diagonal = just_one_number(obter_diagonal(tab, 1), n)
				
				if i in (3, 5, 7):
					ha_diagonal = just_one_number(obter_diagonal(tab, 2), n)	

			if (ha_linha and ha_coluna) or (ha_linha and ha_diagonal) or (ha_coluna and ha_diagonal):
				if defender:
					t += (i,)
				else:
					return i

		if not defender or len(t) == 0:													#se não ouve bifurcacoes
			return 0															

		if len(t) == 1:																#se so exestir uma return
			return t[0]

		for i in tpl:																#ve as defenders possiveis
			tab_aux = tab 															#tab_aux vai é um tabeleiro auxiliar
			ha_diagonal = 0
			ha_linha = 0
			ha_coluna = 0

			l = i // 3 + (i % 3 != 0) - 1											#Calcula a linha da posiçao
			c = i - l * 3 - 1														#Calcula a coluna da posiçao

			ha_linha = just_one_number(obter_linha(tab, l + 1), -n)
			ha_coluna = just_one_number(obter_coluna(tab, c + 1), -n)

			if i % 2 != 0:
				if i in (1, 5, 9):
					ha_diagonal = just_one_number(obter_diagonal(tab, 1), -n)
				
				if i in (3, 7, 5):
					ha_diagonal = just_one_number(obter_diagonal(tab, 2), -n)

			if (ha_linha or ha_diagonal or ha_coluna):								
				tab_aux = marcar_posicao(tab_aux, -n, i)								#marca a posicao no tabuleiro auxiliar
				tpl_aux = obter_posicoes_livres(tab_aux)								#ve as posicoes livres desse tab
				if estrategia_ver_se_falta_uma(tab_aux, -n, tpl_aux) not in t:		#ve se a posiçao nao implica uma birfucacao para o adversário
					return i

		return 0

	#5
	def estrategia_centro(tab, n):
		if eh_posicao_livre(tab, 5):
			return 5

		return 0

	#6
	def estrategia_canto_oposto(tab, n):
		p = (1, 3, 7, 9)
		for i in range(4):					
			j = p[3 - i] // 3 + (p[3 - i] % 3 != 0) - 1								#calcula a linha do canto oposto dai o (3 - i)
			if eh_posicao_livre(tab, p[i]) and -n == tab[j][p[3 - i] - j * 3 -1]:	#ve se o canto oposto esta livre (p[3 - i] - j * 3 -1) calcula a coluna
				return p[i]

		return 0 

	#7
	def estrategia_canto_vazio(tab, n):
		for i in (1, 3, 7, 9):
			if eh_posicao_livre(tab, i):
				return i

		return 0
	#8
	def estrategia_lateral_vazio(tab, n):
		for i in (2, 4, 6, 8):
			if eh_posicao_livre(tab, i):
				return i

		return 0

	tpl = obter_posicoes_livres(tab)

	if st != "basico":
		i = estrategia_ver_se_falta_uma(tab, n, tpl)
		if i:
			return i

		i = estrategia_ver_se_falta_uma(tab, -n, tpl)
		if i:
			return i

	if st == "perfeito":
		i = estrategia_bifurcacao(tab, n, tpl, 0)
		if i:
			return i

		i = estrategia_bifurcacao(tab, -n, tpl, 1)								# ,1) indica que estamos a defender
		if i:
			return i

	i = estrategia_centro(tab, n)
	if i:
		return i

	if st != "basico":
		i = estrategia_canto_oposto(tab, n)
		if i:
			return i

	i = estrategia_canto_vazio(tab, n)
	if i:
		return i

	i = estrategia_lateral_vazio(tab, n)
	if i:
		return i

	return

def jogo_do_galo(st, mode):
	if not isinstance(st, str) or (st != "X" and st != "O")\
	   or not isinstance(mode, str) or mode not in ("basico", "normal", "perfeito"):
		raise ValueError("jogo do galo: algum dos argumentos e invalido")

	print("Bem-vindo ao JOGO DO GALO.")
	print("O jogador joga com '" + st + "'.")

	tab = ((0,0,0),(0,0,0),(0,0,0))

	for i in range(9):
		if st == "O":
			if (i + 1) % 2 != 0:
				print("Turno do computador (" + mode + "):" )
				p = escolher_posicao_auto(tab, 1, mode)
				tab = marcar_posicao(tab, 1, p)
			else:
				p = escolher_posicao_manual(tab)
				tab = marcar_posicao(tab, -1, p)

		else:
			if (i + 1) % 2 == 0:
				print("Turno do computador (" + mode + "):" )
				p = escolher_posicao_auto(tab, -1, mode)
				tab = marcar_posicao(tab, -1, p)
			else:
				p = escolher_posicao_manual(tab)
				tab = marcar_posicao(tab, 1, p)
		
		print(tabuleiro_str(tab))

		if i > 3:
			j = jogador_ganhador(tab)
			if j != 0:
				if j == 1:
					return "X"
				else:
					return "O"

	return "EMPATE"