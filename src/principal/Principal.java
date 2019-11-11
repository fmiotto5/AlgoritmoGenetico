package principal;
//12.1 luger

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Principal extends JFrame implements ActionListener, Runnable{
	JPanel painelPrincipal, painelSouth;
	public JButton btnIniciar, btnParar;
	public JLabel lblGeracao, lblGenesMelhorIndividuo, lblAptidaoMelhorIndividuo, lblMediaAptidao;
	public JCheckBox cbxElitismo;
	public TextField txtfldMutacao, txtfldCrossover, txtfldDelay, txtfldTamPop, txtfldNumMaxGer;
	boolean first = false;
	boolean elitismo = true;
	int tamPop, numMaxGeracoes;
	Canvas canvas = new Canvas();
	Thread t1;
	public static boolean stopThread = false; //o método executa() é executado por uma thread. Se stopThread == true, significa que o usuário abortou a execução do AG
	public static int delay = 0;
	
	public Principal() throws IOException {
		addWindowListener(new WindowAdapter()
		{public void windowClosing(WindowEvent e){System.exit(0);}});
		setSize(720, 550);
		setResizable(false);
		painelPrincipal = new JPanel();
		painelPrincipal.setLayout(new BorderLayout());

		painelSouth = new JPanel(new GridLayout(10,10));
		btnIniciar = new JButton("Iniciar");
		painelSouth.add(btnIniciar);
		btnParar = new JButton("Parar");
		painelSouth.add(btnParar);
		
		txtfldDelay = new TextField("10");
		painelSouth.add(new JLabel("Delay (ms): "));
		painelSouth.add(txtfldDelay);
				
		txtfldCrossover = new TextField("0.8");
		painelSouth.add(new JLabel("Taxa cross.: "));
		painelSouth.add(txtfldCrossover);
		
		txtfldMutacao = new TextField("0.4");
		painelSouth.add(new JLabel("Taxa mutação: "));
		painelSouth.add(txtfldMutacao);
		
		cbxElitismo = new JCheckBox();
		cbxElitismo.setSelected(true);
		painelSouth.add(new JLabel("Elitismo"));
		painelSouth.add(cbxElitismo);
		
		txtfldTamPop = new TextField("4000");
		painelSouth.add(new JLabel("Tamanho pop.: "));
		painelSouth.add(txtfldTamPop);
		
		txtfldNumMaxGer = new TextField("1000");
		painelSouth.add(new JLabel("Nº max. ger.: "));
		painelSouth.add(txtfldNumMaxGer);
		
		lblGeracao = new JLabel("Geração: -");
		painelSouth.add(lblGeracao);
		
		lblAptidaoMelhorIndividuo = new JLabel("Aptidão: -");
		painelSouth.add(lblAptidaoMelhorIndividuo);
		
		painelSouth.add(new JLabel("Aptidão média: "));
		
		lblMediaAptidao = new JLabel("");
		painelSouth.add(lblMediaAptidao);

		painelPrincipal.add(canvas, BorderLayout.CENTER);
		painelPrincipal.add(painelSouth, BorderLayout.EAST);

		Container ct = getContentPane();
		ct.add(painelPrincipal);
		btnIniciar.addActionListener(this);
		btnParar.addActionListener(this);
	}
	
	public void run() {
		Algoritmo.setTaxaDeCrossover(Double.parseDouble(txtfldCrossover.getText()));
        //taxa de muta��o de 3%
        Algoritmo.setTaxaDeMutacao(Double.parseDouble(txtfldMutacao.getText()));
		 //elitismo
        boolean elitismo = true;
        //tamanho da popula��o
        int tamPop = Integer.parseInt(txtfldTamPop.getText());
        //numero máximo de gera��es
        int numMaxGeracoes = Integer.parseInt(txtfldNumMaxGer.getText());

        //define o número de genes do indivíduo baseado na solu��o
        //int numGenes = Algoritmo.getSolucao().length();

        //cria a primeira popula��o aleatérioa
        Populacao populacao = new Populacao(26, tamPop); //26 é o numero de genes

        boolean temSolucao = false;
        int geracao = 0;
        String gene = "";
        
        //loop até critério de parada
		while(!temSolucao && geracao < numMaxGeracoes && !stopThread) { 
			geracao++;
					
			lblGeracao.setText("Geração " + geracao);
			
			//cria nova populacao
			populacao = Algoritmo.novaGeracao(populacao, elitismo);
			
//             força a inserção de uma solução
//            if(geracao == 100) {
//            	populacao.getIndividuo(0).genes = "0101000000010101011010100100000000000101000000110001";
//            	populacao.getIndividuo(0).geraAptidao();
//            }
			
			//verifica se tem a solucao
            temSolucao = populacao.temSolucao();
 
            
            lblAptidaoMelhorIndividuo.setText("Aptidão: " + populacao.getIndividuo(0).getAptidao());
            
            int soma = 0;
            for(int i = 0;i < populacao.getNumIndividuos();i++) {
            	soma += populacao.getIndividuo(i).getAptidao();
            }
            lblMediaAptidao.setText(String.valueOf(soma/populacao.getNumIndividuos()));
//            lblGenesMelhorIndividuo.setText("");
//            for(int i = 0;i < 54;i += 2) {
//            	gene = populacao.getIndividuo(0).getGenes().substring(i, i+2); // gene recebe um conjunto de 2 bits 
//            	switch (gene){
//    			case "00": // Leste	
//    				lblGenesMelhorIndividuo.setText(lblGenesMelhorIndividuo.getText() + "L");
//    				break;
//    			case "01": // Norte				
//    				lblGenesMelhorIndividuo.setText(lblGenesMelhorIndividuo.getText() + "N");
//    				break;
//    			case "10": // Oeste				
//    				lblGenesMelhorIndividuo.setText(lblGenesMelhorIndividuo.getText() + "O");
//    				break;
//    			case "11": // Sul
//    				lblGenesMelhorIndividuo.setText(lblGenesMelhorIndividuo.getText() + "S"); 				
//    				break;
//    			}
//                if (geracao==100)
//                	JOptionPane.showMessageDialog(null, gene);
//            }
            
            
//            principal.repaint();
            canvas.genes = populacao.getIndividuo(0).getGenes();
            canvas.paint(canvas.getGraphics());           
		}
		if(temSolucao)
			JOptionPane.showMessageDialog(this, "Encontrada uma solução.\nNúmero de gerações: " + geracao + "\nAptidão da solução: " + populacao.getIndividuo(0).getAptidao());
		else {
			JOptionPane.showMessageDialog(this, "Número máximo de gerações atingido sem solução encontrada.");

			//
			gene = "";
			String melhor = "";
			for(int i = 0;i < 52;i += 2) {
				gene = populacao.getIndividuo(0).getGenes().substring(i, i+2); // gene recebe um conjunto de 2 bits 
				switch (gene){
				case "00": // Leste	
					melhor += "L";
					break;
				case "01": // Norte				
					melhor += "N";
					break;
				case "10": // Oeste				
					melhor += "O";
					break;
				case "11": // Sul
					melhor += "S";			
					break;
				}
			}
			JOptionPane.showMessageDialog(this, "Configuração dos genes:\n" + melhor);
			//
		}
	}

	public static void main(String[] args) throws IOException {
		Random r = new Random();	
		Principal principal = new Principal();
		principal.setVisible(true);		  
		principal.setTitle("Labirinto");
	}

	public void actionPerformed(ActionEvent evt)
	{
		Object origem = evt.getSource();
		if (origem == btnIniciar) {
			
			//
			Algoritmo.setTaxaDeCrossover(Double.parseDouble(txtfldMutacao.getText()));
	        Algoritmo.setTaxaDeMutacao(Double.parseDouble(txtfldMutacao.getText()));
	        if (cbxElitismo.isSelected())
	        	elitismo = true;
	        else
	        	elitismo = false;
	        //tamanho da popula��o
	        tamPop = Integer.parseInt(txtfldTamPop.getText());
	        //numero máximo de gera��es
	        numMaxGeracoes = Integer.parseInt(txtfldNumMaxGer.getText());	        
	        delay = Integer.parseInt(txtfldDelay.getText());
	        
			this.t1 = new Thread(this);     
			t1.start();
		} else if (origem == btnParar) {
			this.stopThread = true;
			btnIniciar.setEnabled(false);
			btnParar.setEnabled(false);
		}
	}

	
	class Canvas extends JPanel{
		BufferedImage labirinto, ponto;
		String genes = null; //genes do indivíduo que o paintComponent vai desenhar a cada vez que for chamado
		
		public Canvas() throws IOException {
//			this.labirinto = ImageIO.read(new File("/home/felipe/workspace/Labirinto IA/src/labirinto.png"));
//			this.ponto = ImageIO.read(new File("/home/felipe/workspace/Labirinto IA/src/dot.png"));			
			this.labirinto = ImageIO.read(new File("labirinto.png"));
			this.ponto = ImageIO.read(new File("dot.png"));
		}
		
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(labirinto.getScaledInstance(500, 500, 0), 0, 0, null); //desenha tabuleiro
			String gene;
			int x = 0, y = 450;

			if(genes != null) {
				for(int i = 0;i < 52;i += 2) {
					gene = genes.substring(i, i+2); // gene recebe um conjunto de 2 bits	
					g.drawImage(ponto.getScaledInstance(50, 50, 0), x, y, null); //indivíduo começa na posiçção [9][0]

					switch (gene){
					case "00": // Leste	
						x += 50;
						g.drawImage(ponto.getScaledInstance(50, 50, 0), x, y, null);
						break;
					case "01": // Norte		
						y -= 50; 
						g.drawImage(ponto.getScaledInstance(50, 50, 0), x, y, null);
						break;
					case "10": // Oeste	
						x -= 50; 
						g.drawImage(ponto.getScaledInstance(50, 50, 0), x, y, null);
						break;
					case "11": // Sul
						y += 50; 
						g.drawImage(ponto.getScaledInstance(50, 50, 0), x, y, null);
						break;
					}
				}
			}

			try {
				Thread.sleep(Principal.delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
}