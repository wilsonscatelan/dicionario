
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Dicionario extends JFrame {

    private static final long serialVersionUID = 1L;

    //Paleta
    private static final Color BG        = new Color(0xF7F5F0);
    private static final Color PANEL_BG  = new Color(0xFFFFFF);
    private static final Color ACCENT    = new Color(0x2C5F8A);
    private static final Color TEXT_DARK = new Color(0x1A1A2E);
    private static final Color TEXT_LIGHT= new Color(0x9999AA);
    private static final Color BORDER_CLR= new Color(0xDDDDE8);
    private static final Color SEL_BG   = new Color(0xD0E4F5);

    //Fontes
    private static final Font FONT_TITLE = new Font("Serif",     Font.BOLD,  22);
    private static final Font FONT_SUB   = new Font("SansSerif", Font.PLAIN, 11);
    private static final Font FONT_FIELD = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_WORD  = new Font("Serif",     Font.BOLD,  16);
    private static final Font FONT_DEF   = new Font("Serif",     Font.PLAIN, 14);
    private static final Font FONT_HINT  = new Font("SansSerif", Font.ITALIC,13);
    private static final Font FONT_COUNT = new Font("SansSerif", Font.PLAIN, 11);

    //Estado
    private final HashMap<String, String> dicionario = new HashMap<>();
    private DefaultListModel<String> listModel = new DefaultListModel<>();

    //Widgets 
    private JTextField campoBusca;
    private JList<String> listaSugestoes;
    private JLabel labelPalavra;
    private JTextArea areaDefinicao;
    private JLabel labelStatus;
    private JLabel labelContador;
    private JPanel cardPanel;

    public Dicionario() {
        super("Dicionário Português");
        carregarDicionario();
        construirUI();
        setVisible(true);
    }

    //Carregamento
    private void carregarDicionario() {
        String[] caminhos = {
            "dic.txt",
            "./dic.txt",
            System.getProperty("user.dir") + File.separator + "dic.txt"
        };

        for (String caminho : caminhos) {
            File f = new File(caminho);
            if (!f.exists()) continue;
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(f),
                                         Charset.forName("ISO-8859-1")))) {
                String linha;
                while ((linha = br.readLine()) != null) {
                    linha = linha.replace("\r", "").trim();
                    String[] partes = linha.split(" : ", 2);
                    if (partes.length == 2) {
                        dicionario.put(partes[0].trim().toLowerCase(),
                                       partes[1].trim());
                    }
                }
                return;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    //Interface
    private void construirUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820, 620);
        setMinimumSize(new Dimension(680, 500));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        add(criarCabecalho(), BorderLayout.NORTH);
        add(criarPainelCentral(), BorderLayout.CENTER);
        add(criarRodape(), BorderLayout.SOUTH);
    }

    private JPanel criarCabecalho() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ACCENT);
        header.setBorder(new EmptyBorder(18, 28, 18, 28));

        JLabel titulo = new JLabel("Dicionário Português");
        titulo.setFont(FONT_TITLE);
        titulo.setForeground(Color.WHITE);

        int total = dicionario.size();
        String totalStr = total > 0
                ? total + " verbetes carregados"
                : "Coloque o dic.txt na pasta do projeto";
        JLabel subtitulo = new JLabel(totalStr);
        subtitulo.setFont(FONT_SUB);
        subtitulo.setForeground(new Color(0xBBCCDD));

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(subtitulo);
        header.add(textos, BorderLayout.CENTER);
        return header;
    }

    //Painel central
    private JSplitPane criarPainelCentral() {
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                criarPainelEsquerdo(),
                criarPainelDireito());
        split.setDividerLocation(280);
        split.setDividerSize(1);
        split.setBorder(null);
        split.setBackground(BORDER_CLR);
        return split;
    }

    //Painel esquerdo 
    private JPanel criarPainelEsquerdo() {
        JPanel painel = new JPanel(new BorderLayout(0, 0));
        painel.setBackground(PANEL_BG);
        painel.setBorder(new MatteBorder(0, 0, 0, 1, BORDER_CLR));

        // Campo de busca
        JPanel campoPainel = new JPanel(new BorderLayout(8, 0));
        campoPainel.setBackground(PANEL_BG);
        campoPainel.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_CLR),
                new EmptyBorder(12, 14, 12, 14)));

        campoBusca = new JTextField();
        campoBusca.setFont(FONT_FIELD);

        Border innerBorder = new EmptyBorder(6, 10, 6, 10);
        Border outerBorder = new LineBorder(BORDER_CLR, 1, true);
        campoBusca.setBorder(new CompoundBorder(outerBorder, innerBorder));
        campoBusca.setBackground(BG);
        campoBusca.setForeground(TEXT_DARK);

        JLabel icone = new JLabel("Buscar:");
        icone.setFont(FONT_COUNT);
        icone.setForeground(TEXT_LIGHT);

        campoPainel.add(icone, BorderLayout.WEST);
        campoPainel.add(campoBusca, BorderLayout.CENTER);

        //Sugestões
        listaSugestoes = new JList<>(listModel);
        listaSugestoes.setFont(FONT_FIELD);
        listaSugestoes.setBackground(PANEL_BG);
        listaSugestoes.setForeground(TEXT_DARK);
        listaSugestoes.setSelectionBackground(SEL_BG);
        listaSugestoes.setSelectionForeground(ACCENT);
        listaSugestoes.setFixedCellHeight(34);
        listaSugestoes.setBorder(null);
        listaSugestoes.setCellRenderer(new CellRendererPalavra());

        JScrollPane scroll = new JScrollPane(listaSugestoes);
        scroll.setBorder(null);

        labelContador = new JLabel("", SwingConstants.CENTER);
        labelContador.setFont(FONT_COUNT);
        labelContador.setForeground(TEXT_LIGHT);
        labelContador.setBorder(new EmptyBorder(4, 0, 4, 0));
        labelContador.setBackground(BG);
        labelContador.setOpaque(true);

        painel.add(campoPainel,    BorderLayout.NORTH);
        painel.add(scroll,         BorderLayout.CENTER);
        painel.add(labelContador,  BorderLayout.SOUTH);

        // Eventos
        campoBusca.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filtrar(); }
            public void removeUpdate(DocumentEvent e)  { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });

        campoBusca.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    listaSugestoes.requestFocus();
                    if (listModel.size() > 0)
                        listaSugestoes.setSelectedIndex(0);
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String t = campoBusca.getText().trim();
                    if (!t.isEmpty()) mostrarDefinicao(t.toLowerCase());
                }
            }
        });

        listaSugestoes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String sel = listaSugestoes.getSelectedValue();
                if (sel != null) mostrarDefinicao(sel);
            }
        });

        listaSugestoes.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String sel = listaSugestoes.getSelectedValue();
                    if (sel != null) mostrarDefinicao(sel);
                }
            }
        });

        return painel;
    }

    //Painel direito
    private JPanel criarPainelDireito() {
        JPanel painel = new JPanel(new BorderLayout(0, 0));
        painel.setBackground(BG);

        labelPalavra = new JLabel("", SwingConstants.LEFT);
        labelPalavra.setFont(FONT_WORD);
        labelPalavra.setForeground(ACCENT);
        labelPalavra.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_CLR),
                new EmptyBorder(16, 20, 14, 20)));
        labelPalavra.setBackground(PANEL_BG);
        labelPalavra.setOpaque(true);

        areaDefinicao = new JTextArea();
        areaDefinicao.setFont(FONT_DEF);
        areaDefinicao.setForeground(TEXT_DARK);
        areaDefinicao.setBackground(BG);
        areaDefinicao.setLineWrap(true);
        areaDefinicao.setWrapStyleWord(true);
        areaDefinicao.setEditable(false);
        areaDefinicao.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel hint = new JLabel(
                "<html><center>Busque uma palavra<br>na coluna ao lado</center></html>",
                SwingConstants.CENTER);
        hint.setFont(FONT_HINT);
        hint.setForeground(TEXT_LIGHT);

        JScrollPane scroll = new JScrollPane(areaDefinicao);
        scroll.setBorder(null);
        scroll.setBackground(BG);
        scroll.getViewport().setBackground(BG);

        cardPanel = new JPanel(new CardLayout());
        cardPanel.add(hint,   "hint");
        cardPanel.add(scroll, "def");
        ((CardLayout) cardPanel.getLayout()).show(cardPanel, "hint");

        labelStatus = new JLabel(" ");
        labelStatus.setFont(FONT_COUNT);
        labelStatus.setForeground(TEXT_LIGHT);
        labelStatus.setBorder(new EmptyBorder(4, 20, 4, 20));
        labelStatus.setBackground(BG);
        labelStatus.setOpaque(true);

        painel.add(labelPalavra, BorderLayout.NORTH);
        painel.add(cardPanel,    BorderLayout.CENTER);
        painel.add(labelStatus,  BorderLayout.SOUTH);

        return painel;
    }

    //Rodapé 
    private JPanel criarRodape() {
        JPanel rod = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 6));
        rod.setBackground(new Color(0xEEEEF4));
        rod.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_CLR));
        JLabel info = new JLabel(
                "Seta para baixo: navegar  |  Enter: confirmar");
        info.setFont(FONT_COUNT);
        info.setForeground(TEXT_LIGHT);
        rod.add(info);
        return rod;
    }

    //Filtro
    private void filtrar() {
        String texto = campoBusca.getText().trim().toLowerCase();
        listModel.clear();

        if (texto.isEmpty()) {
            labelContador.setText("");
            return;
        }

        dicionario.keySet().stream()
                .sorted()
                .filter(p -> p.startsWith(texto))
                .limit(200)
                .forEach(listModel::addElement);

        if (listModel.size() < 50) {
            dicionario.keySet().stream()
                    .sorted()
                    .filter(p -> !p.startsWith(texto) && p.contains(texto))
                    .limit(100)
                    .forEach(listModel::addElement);
        }

        int n = listModel.size();
        labelContador.setText(n == 0 ? "Nenhum resultado"
                                      : n + " resultado" + (n > 1 ? "s" : ""));

        if (n == 1) mostrarDefinicao(listModel.get(0));
    }

    // ── Definição ────────────────────────────────────────────────────────────
    private void mostrarDefinicao(String palavra) {
        String def = dicionario.get(palavra.toLowerCase());
        labelPalavra.setText(capitalize(palavra));

        if (def != null) {
            areaDefinicao.setText(def);
            areaDefinicao.setCaretPosition(0);
            labelStatus.setText("dic.txt");
            ((CardLayout) cardPanel.getLayout()).show(cardPanel, "def");
        } else {
            areaDefinicao.setText("");
            labelStatus.setText("Palavra não encontrada");
            ((CardLayout) cardPanel.getLayout()).show(cardPanel, "hint");
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    // ── Cell Renderer ────────────────────────────────────────────────────────
    private static class CellRendererPalavra extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;

        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 8));
            label.setFont(new Font("SansSerif", Font.PLAIN, 14));

            if (isSelected) {
                label.setBackground(new Color(0xD0E4F5));
                label.setForeground(new Color(0x2C5F8A));
            } else {
                label.setBackground(index % 2 == 0
                        ? Color.WHITE : new Color(0xFAFAFC));
                label.setForeground(new Color(0x1A1A2E));
            }
            return label;
        }
    }

    // ── Main ─────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(Dicionario::new);
    }
}