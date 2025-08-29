# Estrutura do Projeto

O projeto é composto por três arquivos principais:

## 1. Main.java
- Classe principal do programa
- Inicializa a aplicacão Swing e abre a tela de Login
- Codigo simples que apenas instancia Login e torna a janela visível

## 2. Login.java
- Responsavel pelo cadastro e login de usuarios
- Utiliza CardLayout para alternar entre:
  - Tela inicial
  - Tela de cadastro
  - Tela de login
- Cada usuario possui um arquivo proprio dentro da pasta usuarios, com os seguintes dados:
  1. Senha
  2. Numero de jogos feitos
  3. Partidas ganhas
  4. Partidas perdidas
- Apos login ou cadastro, o jogo TermoGame e iniciado para aquele usuario

## 3. TermoGame.java
- Classe principal do jogo Termo
- Funcionalidades:
  - Sorteia uma palavra secreta de 5 letras a partir de um arquivo palavras.txt
  - Usuario tem 6 tentativas para adivinhar a palavra
  - Sistema de cores:
    - Verde: letra correta e na posicao certa
    - Amarelo: letra correta mas em posicao diferente
    - Cinza: letra nao existe na palavra secreta
  - Estatisticas pessoais exibidas em um painel lateral:
    - Jogos feitos
    - Partidas ganhas
    - Partidas perdidas
  - Estatisticas sao salvas automaticamente no arquivo do usuario ao sair do jogo

---

# Estrutura de Arquivos

projeto-termo 
- Main.java 
- Login.java 
- TermoGame.java 
- usuarios  pasta criada automaticamente para salvar os cadastros 
  - usuario.txt  exemplo de arquivo de usuario 
- palavras.txt  lista de palavras de 5 letras usadas no jogo

---

# Como Executar

1. Compile os arquivos Java: 
   ```bash
   javac Main.java Login.java TermoGame.java
   ```

2. Execute o programa: 
   ```bash
   java Main
   ```

3. O jogo abrira com a tela inicial Login Cadastro

# Alternativa 2 de Compilação

1. make

---

# Regras do Jogo

- A palavra secreta sempre tera 5 letras
- O jogador tem 6 tentativas para acertar
- Digite uma letra por celula e pressione Enter para validar
- A cada tentativa as letras sao coloridas de acordo com a correspondencia com a palavra secreta
- Se vencer ou perder pode iniciar uma nova partida

---

# Exemplo de Arquivo palavras.txt

O arquivo deve conter uma lista de palavras de 5 letras, uma por linha:

TERMO
JOGOS 
CORES 
LINHA 
VERDE 

Apenas palavras com exatamente 5 letras serao consideradas

---

# Funcionalidades Futuras

- Ranking de usuarios
- Suporte a palavras maiores (6 ou mais letras)
- Exportar estatisticas em CSV ou JSON
- Melhorar a interface grafica

---

# Autores

Cauã Lemos Câmara - 24.1.4035
Kevin Caley Lauar Ferreira Quimatzoyaro - 24.1.4023
Roberto Antonino Menegassi Filho - 24.1.4002


