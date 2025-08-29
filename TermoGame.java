import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class TermoGame extends JFrame {

    //Variáveis principais(palavra para adivinhar e estatísticas)
    private String palavraSecreta;
    private int jogosFeitos = 0;
    private int partidasGanhas = 0;
    private int partidasPerdidas = 0;

    //Número de tentativas do jogador
    private int tentativasRestantes = 6;
    
    //Qual linha o jogador está atualmente (0 a 4)
    private int linhaAtual = 0;

    //Labels das estatísticas
    private JLabel lblJogosFeitos;
    private JLabel lblPartidasGanhas;
    private JLabel lblPartidasPerdidas;

    //Tabuleiro de jogo completo, com todas as tentativas de uma partida, vazias ou não
    private JTextField[][] tabuleiro = new JTextField[6][5];

    //Botão que cria uma nova tentativa/partida
    //Reseta tentativas, tabuleiro e escolhe outra palavra
    private JButton btnNovaPartida;

    //Armazena o banco de palavras (palavras.txt)
    //Usado para validação
    private Set<String> bancoPalavras = new HashSet<>();

    //Também armazena o banco de palavras, mas é usado para sortear a palavra da partida
    private java.util.List<String> listaPalavras = new ArrayList<>();
    private Random random = new Random();

    public TermoGame(File arquivo) {
        //Nomeia a janela
        super("Termo");
        //Encerra ao clicar em 'x'
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //Abre a janela já maximizada
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        //Componentes visuais divididos em NORTH, SOUTH, EAST, WEST
        setLayout(new BorderLayout());

        //Carrega palavras
        carregarBancoDePalavras();

        //Carrega dados de partidas anteriores
        carregarEstatisticas(arquivo);

        //Sorteia a palavra da partida
        escolherNovaPalavraSecreta();

        // ---------------- Estatísticas ----------------
        //Cria o painel de estatísticas
        JPanel panelStats = new JPanel();
        panelStats.setBackground(new Color(153, 51, 255));
        //Organza os dados verticalmente
        panelStats.setLayout(new BoxLayout(panelStats, BoxLayout.Y_AXIS));
        //largura 200 pixels, altura padrão (cobre toda a janela)
        panelStats.setPreferredSize(new Dimension(400, getHeight()));
        //Coloca uma borda vísivel, com o nome do painel, para definição do título
        panelStats.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Estatísticas", 
        TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 50), Color.WHITE));

        //Coração das estatísticas, exibe os dados armazenados
        lblJogosFeitos = new JLabel("Jogos feitos: " + jogosFeitos);
        lblJogosFeitos.setFont(new Font("Arial", Font.BOLD, 30));
        lblJogosFeitos.setForeground(Color.WHITE);
        lblPartidasGanhas = new JLabel("Partidas ganhas: " + partidasGanhas);
        lblPartidasGanhas.setFont(new Font("Arial", Font.BOLD, 30));
        lblPartidasGanhas.setForeground(Color.WHITE);
        lblPartidasPerdidas = new JLabel("Partidas perdidas: " + partidasPerdidas);
        lblPartidasPerdidas.setFont(new Font("Arial", Font.BOLD, 30));
        lblPartidasPerdidas.setForeground(Color.WHITE);

        //Coloca dos labels no painel de estatísticas
        panelStats.add(lblJogosFeitos);
        panelStats.add(lblPartidasGanhas);
        panelStats.add(lblPartidasPerdidas);

        //Aplica o painel no JFrame, à direta(WEST)
        add(panelStats, BorderLayout.EAST);

        JPanel style = new JPanel();
        style.setSize(new Dimension(800, getHeight()));
        style.setBackground(new Color(153, 51, 255));
        add(style, BorderLayout.WEST);

        // ---------------- Tabuleiro ----------------
        //Cria o peinel do Tabuleiro(real jogo) em forma de Grid 5x5
        //Espaçamento de 5 pixels entre cada quadrado de letra
        JPanel panelTabuleiro = new JPanel(new GridLayout(6, 5, 5, 5));
        panelTabuleiro.setBackground(new Color(153, 51, 255));
        panelTabuleiro.setPreferredSize(new Dimension(400, 400));
        //For aninhando do tabuleiro 5x5
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                //Cria um input cell para texto
                JTextField cell = new JTextField();

                //Faz a letra do input ser o centro da célula
                cell.setHorizontalAlignment(JTextField.CENTER);
                //Formata a fonte, tamanho e tipo(negrito)
                cell.setFont(new Font("Arial", Font.BOLD, 24));
                //Só permite uma letra por célula
                cell.setDocument(new JTextFieldLimit(1));
                //Define a cor do fundo da célula
                cell.setBackground(Color.LIGHT_GRAY);
                //Define a cor da borda da célula
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                //Salva linha e coluna atual
                final int linha = i;
                final int coluna = j;

                //Key listener específico da célula
                cell.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        //Limita a ligitação somente à linha atual
                        if (linha != linhaAtual) { e.consume(); return; }
                        //Coleta o char
                        char c = e.getKeyChar();
                        //Permite apenas o input de uma letra
                        if (!Character.isLetter(c)) { e.consume(); return; }
                        //Coloca todos os carateres em uppercase(maíusculo), para facilitar verificação
                        e.setKeyChar(Character.toUpperCase(c));

                        SwingUtilities.invokeLater(() -> {
                            //Após digitar, se ainda houver letras a serem escritas
                            //Move o "foco"(ponteiro para digitação), para a próxima célula
                            if (coluna < 4) tabuleiro[linhaAtual][coluna + 1].requestFocus();
                        });
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                        //Mesmo limitador da função anterior
                        if (linha != linhaAtual) {
                            e.consume();
                            return;
                        }

                        //Caso for apagar a letra
                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            //Se a célula do foco estiver vazia e não estiver na coluna inicial
                            if (cell.getText().isEmpty() && coluna > 0) {
                                //Pede o foco para a letra anterior e apaga a letra
                                tabuleiro[linhaAtual][coluna - 1].requestFocus();
                                tabuleiro[linhaAtual][coluna - 1].setText("");
                            }
                        }

                        //Caso a tecla apertada seja enter
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            //Confere se todas, as letras foram preenchidas
                            //Se a palavra está em "palavras.txt", etc
                            verificarTentativa(arquivo);
                        }
                    }
                });
                //Guarda o textField(cell) na matriz tabuleiro, para conferência e correção
                tabuleiro[i][j] = cell;
                //Adiciona a celula(JTextField) ao tabuleiro(JPanel)
                panelTabuleiro.add(cell);
            }
        }
        //Adiciona o tabuleiro(JPanel) à janela(JFrame), no centro da tela
        add(panelTabuleiro, BorderLayout.CENTER);

        // ---------------- Botões ----------------
        //Novo painel para os botões de Nova Partida e Sair do Jogo
        JPanel panelBotoes = new JPanel(new FlowLayout());
        panelBotoes.setSize(new Dimension(400, getWidth()));
        panelBotoes.setBackground(new Color(153, 204, 255));

        //Define o botão de nova partida
        btnNovaPartida = new JButton("Nova Partida");

        //"Esconde" o botão
        btnNovaPartida.setVisible(false);

        //Action Listener, caso pressionado, reinicia o jogo
        btnNovaPartida.addActionListener(e -> {
            reiniciarJogo();
        });

        //Botão para sair do jogo
        JButton btnSair = new JButton("Sair do Jogo");

        //Action listener para o botão sair
        btnSair.addActionListener(e -> {
            //Caso pressionado abre uma janela que mostra as estatísticas
            JOptionPane.showMessageDialog(this,
                    "Estatísticas finais:\n" +
                            "Jogos feitos: " + jogosFeitos + "\n" +
                            "Partidas ganhas: " + partidasGanhas + "\n" +
                            "Partidas perdidas: " + partidasPerdidas
            );
            salvarEstatisticas(arquivo);
            //Sai do jogo
            System.exit(0);
        });

        //Adiciona os botões de nova partida e sair no painel
        panelBotoes.add(btnNovaPartida);
        panelBotoes.add(btnSair);
        //Posiciona o painel no topo da janela (JFrame)
        add(panelBotoes, BorderLayout.NORTH);

        //Caso qualquer um dos botões seja pressionado, o foco/cursor vai para o primeiro JTextFeild
        tabuleiro[0][0].requestFocus();
    }

    private void carregarBancoDePalavras() {
        //Abre palavras.txt
        //Uso de try para fechamento automático do arquivo, mesmo em caso de erro
        //Uso de Buffered Reader para ler linha por linha mais eficientemente
        try (BufferedReader br = new BufferedReader(new FileReader("palavras.txt"))) {
            //Armazena a palavra da linha
            String linha;
            //Copia a linha do buffer para a String linha
            //Quando acabam as linhas o buffer retorna null e o loop se encerra
            while ((linha = br.readLine()) != null) {
                //Remove todos os espaços(trim) e converte todas as letras para UPPERCASE e remove acentos
                //Evita diferenças entre palavras como termo e TERMO
                String word = Normalizer.normalize(linha.trim().toUpperCase(), Normalizer.Form.NFD);
                Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
                String palavra = pattern.matcher(word).replaceAll("");
                
                //String palavra = linha.trim().toUpperCase();

                //Se a palavra não tiver EXATAMENTE 5 letras, ela não é lida
                if (palavra.length() == 5) {
                    bancoPalavras.add(palavra);
                    listaPalavras.add(palavra);
                }
            }
            //Captura qualquer erro
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar palavras.txt: " + e.getMessage());
        }
    }

    private void escolherNovaPalavraSecreta() {
        //Caso alguma palavra tenha sido carregada
        if (!listaPalavras.isEmpty()) {
            //Seleciona uma palavra, baseado num int aleatório
            palavraSecreta = listaPalavras.get(random.nextInt(listaPalavras.size()));
        } else {
            //Define termo como palavra padrão, caso nenhuma tenha sido carregada
            palavraSecreta = "TERMO"; // fallback
        }
    }


    private void verificarTentativa(File arquivo) {
        //String builder que juntará as letras para contruir uma tentativa válida
        StringBuilder tentativa = new StringBuilder();
        //Percorre letra por letra da tentativa
        for (int j = 0; j < 5; j++) {

            String letra = tabuleiro[linhaAtual][j].getText();
            //Caso algum espaço para letra não tenha sido preenchido cancela a verificação
            if (letra.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todas as letras da linha!");
                return;
            }
            //Aplica a letra no final da String tentativa em uppercase
            tentativa.append(letra.toUpperCase());
        }

        //Copia o conteúdo de tentativa para palavraTentativa, usando conversão toString
        String palavraTentativa = tentativa.toString();

        // Validação no banco de palavras
        if (!bancoPalavras.contains(palavraTentativa)) {
            //Caso a palavra da tentativa não esteja no banco de palavras
            JOptionPane.showMessageDialog(this, "Palavra inválida!");
            return;
        }

        //Após todas as verificações de exceções, finalmente, realiza os processos para uma palavra válida

        //Letras verdes
        int[] usados = new int[5];
        //Percorre todas as letras da tentativa
        for (int j = 0; j < 5; j++) {
            //Ponteiro para a célula atual
            JTextField cell = tabuleiro[linhaAtual][j];
            cell.setForeground(Color.BLACK);
            //Copia o char da tentativa atual
            char c = palavraTentativa.charAt(j);
            //Caso o caractere seja o mesmo da palavra a ser adivinhada
            //E esteja na mesma posição, o JTextField será colorido de verde
            if (c == palavraSecreta.charAt(j)) {
                cell.setBackground(Color.GREEN);
                //Marca usados para evitar repetição de letras já marcadas
                usados[j] = 1;
            }
        }

        //Letras amarelas
        for (int j = 0; j < 5; j++) {
            //Mesmo processo das letras amarelas
            JTextField cell = tabuleiro[linhaAtual][j];
            char c = palavraTentativa.charAt(j);

            //Ignora células já verdes
            if (cell.getBackground() == Color.GREEN) continue;

            boolean marcado = false;
            for (int k = 0; k < 5; k++) {
                //Se a letra for a de palavra secreta, mas não estiver marcada
                //Ou seja, não foi verde, fica amarela
                if (palavraSecreta.charAt(k) == c && usados[k] == 0) {
                    cell.setBackground(Color.YELLOW);
                    //Seta como usada
                    usados[k] = 1;
                    //E como marcada
                    marcado = true;
                    break;
                }
            }
            //Se não foi marcada, nem usada, é mantida a cor padrão
            if (!marcado) cell.setBackground(Color.GRAY);
        }

        //Deixa o botão de nova partida visível, após verificação da primeira letra
        btnNovaPartida.setVisible(true);

        // Vitória
        if (palavraTentativa.equals(palavraSecreta)) {
            //Altera as estatísticas
            partidasGanhas++;
            jogosFeitos++;
            atualizarEstatisticas();
            JOptionPane.showMessageDialog(this, "Parabéns! Você acertou a palavra!");
            return;
        }

        linhaAtual++;
        tentativasRestantes--;

        // Derrota
        if (tentativasRestantes == 0) {
            partidasPerdidas++;
            jogosFeitos++;
            atualizarEstatisticas();
            salvarEstatisticas(arquivo);
            JOptionPane.showMessageDialog(this, "Fim de jogo! A palavra era: " + palavraSecreta);
        } else {
            tabuleiro[linhaAtual][0].requestFocus();
        }
    }

    //Caso o botão de reiniciar seja clicado
    private void reiniciarJogo() {
        //Retorna para a primeira linha
        linhaAtual = 0;
        //Reinicia as tentativas
        tentativasRestantes = 6;
        //Sorteia uma nova palavra
        escolherNovaPalavraSecreta();

        //Reinicia o tabuleiro
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                tabuleiro[i][j].setText("");
                tabuleiro[i][j].setBackground(Color.LIGHT_GRAY);
            }
        }
        //Mantém o foco na primeira letra da primeira palavra
        tabuleiro[0][0].requestFocus();
        btnNovaPartida.setVisible(false);
    }

    //Altera as estatísticas
    //Usado sempre que um novo jogo é começado, ou um jogo é finalizado
    private void atualizarEstatisticas() {
        lblJogosFeitos.setText("Jogos feitos: " + jogosFeitos);
        lblPartidasGanhas.setText("Partidas ganhas: " + partidasGanhas);
        lblPartidasPerdidas.setText("Partidas perdidas: " + partidasPerdidas);
    }

    //Limita uma letra por campo
    static class JTextFieldLimit extends javax.swing.text.PlainDocument {
        //Armazena o número máximo de caracteres (1)
        private final int limit;

        //Construtor
        JTextFieldLimit(int limit) { this.limit = limit; }

        //Chamado sempre, ao inserir um texto em JTextField
        public void insertString(int offset, String str, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
            //Se não houver texto
            if (str == null) return;
            //Se texto inserido for de tamanho válido, insere a string
            if ((getLength() + str.length()) <= limit) super.insertString(offset, str, attr);
        }
    }


    private void carregarEstatisticas(File file) {
        //Criação de obj scanner com try para ler informações e capturar possíveis erros
        try (Scanner sc = new Scanner(file)){
            // Ignora a senha, já verificada
            if (sc.hasNextLine()) sc.nextLine();
            //Captura o número de jogos, caso haja um int a ser lido
            if (sc.hasNextInt()) jogosFeitos = sc.nextInt();
            //Captura as vitórias
            if (sc.hasNextInt()) partidasGanhas = sc.nextInt();
            //Captura as derrotas
            if (sc.hasNextInt()) partidasPerdidas = sc.nextInt();
        } catch (IOException e) {
            //Detalha a exceção capturada
            e.printStackTrace();
        }
    }

    // Salva dados sobre os jogos (tentativas, vitórias e derrotas) no arquivo do usuário
    private void salvarEstatisticas(File arquivoUsuario) {
        if (arquivoUsuario == null || !arquivoUsuario.exists()) {
            JOptionPane.showMessageDialog(this, "Arquivo do usuário inválido!");
            return;
        }

        try {
            // Lê todas as linhas do arquivo do usuário
            java.util.List<String> linhas = new java.util.ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(arquivoUsuario))) {
                String linha;
                while ((linha = br.readLine()) != null) {
                    linhas.add(linha);
                }
            }

            // Garante que o arquivo tenha pelo menos 4 linhas
            while (linhas.size() < 4) {
                linhas.add("0");
            }

            // Substitui as linhas de estatísticas (linhas 2,3,4)
            linhas.set(1, String.valueOf(jogosFeitos));
            linhas.set(2, String.valueOf(partidasGanhas));
            linhas.set(3, String.valueOf(partidasPerdidas));

            // Escreve novamente no arquivo
            try (PrintWriter pw = new PrintWriter(new FileWriter(arquivoUsuario))) {
                for (String l : linhas) {
                    pw.println(l);
                }
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar estatísticas: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
