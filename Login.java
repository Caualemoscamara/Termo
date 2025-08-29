import java.awt.*;
import java.io.*;
import javax.swing.*;

public class Login extends JFrame{
    // Arquivo passado para salvamento de dados do usuário
    private File ll;

    // Construtor
    public Login(){
        // Tílulo da janela inicial
        super("Termo: Tela Inicial");
        // Fecha com o 'X'
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // O programa já inicia cobrindo toda a tela
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        //Uso de border layout no JFrame
        setLayout(new BorderLayout());

        //JPanel central, com card Layout
        //Responsável por alternar entre telas de cadastro, login e início livremente
        JPanel painelPrincipal = new JPanel(new CardLayout());

        //Permite usar as funções específicas do card layout, fazendo conversão de tipo, por segurança apenas
        CardLayout cl = (CardLayout)(painelPrincipal.getLayout());
        
        // --------------- Tela para criar uma conta ----------------------
        // Seta um painel como GridBagLayout
        // Esse layout fornece uma liberdade melhor ao posicionar itens, como JTextFields
        // JLabels, etc. Usando um sistema simples de coordenadas (x, y);
        JPanel cadastro = new JPanel(new GridBagLayout());

        //Grid invisível que permite o posicionamento dos itens
        GridBagConstraints grid = new GridBagConstraints();

        //Define o espaçamento mínimo entre cada componente, 5 pixels em todas as direções
        grid.insets = new Insets(5, 5, 5, 5);

        /* Label usuário */
        // Coordenadas (0, 0);
        grid.gridx = 0;
        grid.gridy = 0;
        //Cria um label escrito usuário, mais tarde será adicionado um JTextField para uso
        JLabel user = new JLabel("Usuário:");
        //Formata a fonte e cor da escrita
        user.setFont(new Font("Arial", Font.BOLD, 50));
        user.setForeground(Color.WHITE);
        //Adiciona o JLabel
        cadastro.add(user, grid);

        /*Campo usuário */
        // Coordenada (1, 0) 0 já é predefinido por padrão devido ao zero do componente anterior
        grid.gridx = 1;
        // JTextField para definição de um novo nome para o usuário
        JTextField uss = new JTextField();
        // Formatação da dimensão e fonte do texto inserido pelo usuário
        uss.setPreferredSize(new Dimension(300, 50));
        uss.setFont(new Font("Arial", Font.BOLD, 30));
        // Adiciona o JTextField ao JPanels
        cadastro.add(uss, grid);

        /* Label senha */
        // Coordenadas (0, 1)
        grid.gridx = 0;
        grid.gridy = 1;
        // JLabel escrito Senha
        JLabel pass = new JLabel("Senha:");
        // Formatação da escrita
        pass.setFont(new Font("Arial", Font.BOLD, 50));
        pass.setForeground(Color.WHITE);
        // Adiciona o JTextField ao Jpanel
        cadastro.add(pass, grid);

        // Campo senha
        // Coordenadas (1, 1) y não definido pelo mesmo motivo anterior
        grid.gridx = 1;
        //JPasswordField para a senha criada pelo usuário
        JPasswordField senn = new JPasswordField();
        //Formatação
        senn.setPreferredSize(new Dimension(300, 50));
        senn.setFont(new Font("Arial", Font.BOLD, 30));
        cadastro.add(senn, grid);

        // Botão login
        JButton btnConfirmar = new JButton("Confimar");
        //Configura o botão confirmar (tamanho)
        btnConfirmar.setPreferredSize(new Dimension(150, 50));
        //Ação do botão confirmar
        btnConfirmar.addActionListener(e ->{
            // Coleta os dados para a criação de um novo arquivo para o usuário
            // Essa função retorna um int apenas para verificação de sucesso ou falha na função
            int dec = salvarCadastro(uss, senn);
            // Em caso de sucesso, realiza os seguintes processos
            if (dec == 0) {
                //Cria o arquivo
                File arqUsuario = getFile();
                //Em caso do arquivo exitir e não for nulo
                if (arqUsuario != null && arqUsuario.exists()) {
                    //Encerra as telas para cadastro e inicia o jogo, na conta digitada
                    TermoGame game = new TermoGame(arqUsuario);
                    game.setVisible(true);
                    //Fecha a janela do cadastro
                    this.dispose();
                } else {
                    //Trata erro caso o arquivo falhe na criação
                    JOptionPane.showMessageDialog(this, "Erro: arquivo do usuário não encontrado!");
                }
            }
            
        });
        //Insere as coordenadas do botão confirmar (0, 2)
        grid.gridx = 0;
        grid.gridy = 2;
        grid.gridwidth = 2;
        //Seta o lado preferido para o botão (Esquerda)
        grid.anchor = GridBagConstraints.WEST;
        //Adiciona o botão no JPanel
        cadastro.add(btnConfirmar, grid);

        //Cria o botão cancelar
        JButton btnVoltar = new JButton("Cancelar");
        //Formata o botão
        btnVoltar.setPreferredSize(new Dimension(150, 50));
        //Caso cancelar for selecionado, apaga todos os campos e retorna a tela inicial
        btnVoltar.addActionListener(e ->{
            uss.setText("");
            senn.setText("");
            // cl é usado para alternar entre os paineis criados, semelhante
            // a uma apresentação de slides, nesse caso, apresenta a tela de início
            cl.show(painelPrincipal, "Tela Inicial");
        });
        // Insere as coordenadas do botão (1, 2)
        grid.gridx = 1;
        grid.gridy = 2;
        //Insere o lado preferido do botão (direita)
        grid.anchor = GridBagConstraints.EAST;
        // Muda a cor do JPanel e adiciona o botão ao painel
        cadastro.setBackground(new Color(51, 204, 204));
        cadastro.add(btnVoltar, grid);

        //Adiciona Esse painel ao painel principal, um cardLayout que controla quais painéis irão aparecer
        painelPrincipal.add(cadastro, "Cadastro");

        //---------------- Opções Entrar (Caso uma conta já foi criada) -------------------------
        // Cria mais um painel, que será adicionado ao confundo do cardLayout futuramente
        JPanel log = new JPanel(new GridBagLayout());
        // Configurações idênticas ao do painel anterior
        GridBagConstraints gr = new GridBagConstraints();
        gr.insets = new Insets(5, 5, 5, 5);

        /* Label usuário */
        // Coordenadas do JLabel
        gr.gridx = 0;
        gr.gridy = 0;
        // Cria o JLabel
        JLabel user2 = new JLabel("Usuário:");
        // Configura
        user2.setFont(new Font("Arial", Font.BOLD, 50));
        user2.setForeground(Color.WHITE);
        // Adiciona
        log.add(user2, gr);

        /* Campo usuário */
        // Coordenadas do JLabel
        gr.gridx = 1;
        // Cria o campo para texto
        JTextField uss2 = new JTextField();
        // Configura
        uss2.setPreferredSize(new Dimension(300, 50));
        uss2.setFont(new Font("Arial", Font.BOLD, 30));
        // Adiciona
        log.add(uss2, gr);

        /* Label senha */
        // Coordenadas
        gr.gridx = 0;
        gr.gridy = 1;
        // Cria o JLabel da senha
        JLabel pass2 = new JLabel("Senha:");
        // Configura
        pass2.setFont(new Font("Arial", Font.BOLD, 50));
        pass2.setForeground(Color.WHITE);
        // Adiciona
        log.add(pass2, gr);

        /* Campo senha */
        // Coordenadas do campo
        gr.gridx = 1;
        // Novo campo para senha
        JPasswordField senn2 = new JPasswordField();
        // Formatação do campo
        senn2.setFont(new Font("Arial", Font.BOLD, 30));
        senn2.setPreferredSize(new Dimension(300, 50));
        // Adiciona ao JPanel
        log.add(senn2, gr);

        /* Botão login */
        // Cria botão de confirmar
        JButton btnConf = new JButton("Confimar");
        // Adiciona a função do botão de confirmar
        btnConf.addActionListener(e ->{
            // Carrega os dados do usuário (procura pelo arquivo)
            int ver = carregarDadosUsuario(uss2, senn2);
            // Inicia os processos essenciais apenas em caso de sucesso no carregamento
            if (ver == 0) {
                // Cria o arquivo
                File arqUsuario = getFile();
                // Em caso de sucesso no carregamento
                // Se a referência não for nula e o arquivo existir
                if (arqUsuario != null && arqUsuario.exists()) {
                    // Inicia o jogo em si
                    TermoGame game = new TermoGame(arqUsuario);
                    game.setVisible(true);
                    // Fecha a tela de login
                    this.dispose();
                } else {
                    // Mensagem de erro
                    JOptionPane.showMessageDialog(this, "Erro: arquivo do usuário não encontrado!");
                }
            }
        });
        // Configura o botão confirmar
        btnConf.setPreferredSize(new Dimension(150, 50));
        // Seta as coordenadas dele
        gr.gridx = 0;
        gr.gridy = 2;
        gr.gridwidth = 2;

        // Seleciona a posição
        gr.anchor = GridBagConstraints.WEST;
        // Adiciona o botão
        log.add(btnConf, gr);

        // Cria o botão de cancelar
        JButton btnVoltar2 = new JButton("Cancelar");
        // Configura
        btnVoltar2.setPreferredSize(new Dimension(150, 50));
        // Esse botão apaga todas as informações digitadas em campos e retorna a tela inicial
        btnVoltar2.addActionListener(e ->{
            uss2.setText("");
            senn2.setText("");
            cl.show(painelPrincipal, "Tela Inicial");
        });
        // Coloca as coordenadas do botão
        gr.gridx = 1;
        gr.gridy = 2;
        // Posição dele na coordenada
        gr.anchor = GridBagConstraints.EAST;
        // Configurações de fonte
        log.setBackground(new Color(51, 204, 204));

        // Adiciona o botão e o subPainel ao painel principal (CardLayout)
        log.add(btnVoltar2, gr);
        painelPrincipal.add(log, "Login");
        //add(log, BorderLayout.CENTER);

        // ----------------- Tela Início -----------------------
        // Cria o painel e o define como boxLayout vertica
        // Isso significa que os componentes são adicionados um embaixo do outro
        JPanel options = new JPanel();
        options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));

        //Cria o label com o título do jogo
        JLabel termo = new JLabel("TERMO");
        // Configurações de fonte, cor e alinhamento
        termo.setForeground(Color.WHITE);
        termo.setFont(new Font("Arial", Font.BOLD, 90));
        termo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Adiciona o botão criar conta (cadastro)
        JButton btnCadastro = new JButton("Criar Conta");
        // Configura a fonte
        btnCadastro.setFont(new Font("Arial", Font.BOLD, 30));
        btnCadastro.setPreferredSize(new Dimension(300, 100));
        btnCadastro.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Mostra o painel de cadastro
        btnCadastro.addActionListener(e -> {
            cl.show(painelPrincipal, "Cadastro");
        });

        // Botão para entrar em uma conta já criada
        JButton btnLogin = new JButton("     Entrar     ");
        // Configura o botão em fonte, tamanho e alinhamento
        btnLogin.setFont(new Font("Arial", Font.BOLD, 30));
        btnLogin.setPreferredSize(new Dimension(300, 100));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Função do botão é direcionar o usuário para a próxima tela
        btnLogin.addActionListener(e -> {
            cl.show(painelPrincipal, "Login");
        });

        // Cria o botão de sair (espaços são feitos para melhorar o layout)
        JButton btnOut = new JButton("       Sair       ");
        // Configura o botão
        btnOut.setFont(new Font("Arial", Font.BOLD, 30));
        btnOut.setPreferredSize(new Dimension(300, 100));
        btnOut.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Função do botão é encerrar o programa
        btnOut.addActionListener(e -> {
            System.exit(0);
        });
        
        // Formata o espaçamento entre os botões
        // VerticalStrut adiciona um espaço fixo
        // VerticalGlue adiciona um espaço customizável, que reduz e aumenta automaticamente
        options.add(Box.createVerticalStrut(60));
        options.add(termo);
        options.add(Box.createVerticalGlue());
        options.add(btnCadastro);
        options.add(Box.createVerticalStrut(40));
        options.add(btnLogin);
        options.add(Box.createVerticalStrut(40));
        options.add(btnOut);
        options.add(Box.createVerticalGlue());

        // Formata o background do último painel das telas de registro
        options.setBackground(new Color(51, 204, 204));
        //Adiciona o painel de opções ao painel principal
        painelPrincipal.add(options, "Tela Inicial");
        // Mostra a tela inicial
        cl.show(painelPrincipal, "Tela Inicial");
        // Aciciona o painel principal(com todos os sub Painéis internos)
        add(painelPrincipal, BorderLayout.CENTER);
    }

    // Método para salvar as informações do usuário
    private int salvarCadastro(JTextField campoUsuario, JPasswordField campoSenha) {
        //Coleta o nome e senha dos JFields
        String usuario = campoUsuario.getText().trim();
        String senha = new String(campoSenha.getPassword()).trim();

        // Caso algum campo esteja vazio (ou ambos)
        if (usuario.isEmpty() || senha.isEmpty()) {
            // Mensagem de erro no preenchimento
            JOptionPane.showMessageDialog(this, "Preencha usuário e senha!");
            return -1;
        }

        // Cria uma pasta para os usuários, caso ela ainda não exista
        File pastaUsuarios = new File("usuarios");
        if(!pastaUsuarios.exists()){
            pastaUsuarios.mkdir();
        }

        // Cria o arquivo dentro da pasta, o nome do arquivo é o nome do usuário + txt
        File arquivo = new File(pastaUsuarios, usuario + ".txt");
        // Se o arquivo já existir
        if(arquivo.exists()){
            JOptionPane.showMessageDialog(this, "Usuário já existe!");
            return -1;
        }

        // Seta todos os valores nas linhas para 0, menos o da senha
        // Isso ajuda o carregaArquivos a ler corretamente e zerar os
        // Dados de usuário sem histórico
        try (PrintWriter pw = new PrintWriter(arquivo)) {
            pw.println(senha);
            pw.println("0");     
            pw.println("0");     
            pw.println("0");
            // Mostra sucesso no cadastro
            JOptionPane.showMessageDialog(this, "Cadastro salvo com sucesso!");
        } catch (IOException e) {
            // Tratamento de qualquer possível erro
            JOptionPane.showMessageDialog(this, "Erro ao salvar cadastro: " + e.getMessage());
            e.printStackTrace();
        }
        // Altera a referência para o arquivo
        ll = arquivo;
        return 0;
    }
    // Carrega os dados de um usuário
    private int carregarDadosUsuario(JTextField campoUsuario, JPasswordField campoSenha) {
        // Coleta o nome e senha do usuário nos campos
        String usuario = campoUsuario.getText().trim();
        String senha = new String(campoSenha.getPassword()).trim();

        // Cria a referência para o arquivo com o nome do usuário
        File arquivo = new File("usuarios", usuario + ".txt");

        // Se o arquivo não existir
        if (!arquivo.exists()) {
            JOptionPane.showMessageDialog(this, "Usuário não encontrado!");
            return -1;
        }
        // Lê linha por linha, e confere se a senha está correta
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String senhaArq = br.readLine(); // primeira linha = senha
            if (!senhaArq.equals(senha)) {
                JOptionPane.showMessageDialog(this, "Senha incorreta!");
                return -1;
            }
        } catch (IOException e) {
            // Captura qualquer possível erro
            JOptionPane.showMessageDialog(this, "Erro ao ler arquivo: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
        // retorna a referência do arquivo
        ll = arquivo;
        return 0;
    }

    public File getFile(){
        return ll;
    }
}