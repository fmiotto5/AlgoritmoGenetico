package principal;
import java.util.Random;

public class Individuo {

	public String genes = "";
	private int aptidao = 0;
	static String[][] labirinto = new String[][] {{"1001","1010","1010","1010","0011","1001","1010","1010","0011","1000"},
												  {"0101","1000","1010","1010","0110","0101","1001","0011","1100","0110"},
												  {"1101","1010","1010","1010","1010","0111","0100","0101","1010","0011"}, 
												  {"1100","1010","1010","0011","1000","1110","1010","0110","0001","0101"},
												  {"1001","0010","1001","0111","1001","1010","1010","1010","0110","0100"},
												  {"1100","1010","0111","1101","1110","1010","1010","1010","1010","0011"},
												  {"1001","1010","0110","0111","1000","1011","0011","1001","1010","0100"},
												  {"1101","1011","1010","1110","1011","0111","0101","0100","1010","0011"},
												  {"0101","0101","0000","0000","0101","0101","1100","1010","1010","0111"},
												  {"0100","1100","1010","1010","0110","1100","1010","1010","1010","0110"}};											 

//LNOS	
//static String[][] labirinto = new String[][] {{"1001","1010","1010","1010","0011","1001","1010","1010","0011","1000"},
//											  {"0101","1000","1010","1010","0110","0101","1001","0011","1100","0110"},
//											  {"1101","1010","1010","1010","1010","0111","0100","0101","1110","0011"}, 
//											  {"1100","1010","1010","0011","1000","1110","1010","0110","0101","0101"},
//											  {"1001","0010","1001","0111","1101","1010","1010","1010","0110","0100"},
//											  {"1100","1010","0111","1101","1110","1010","1010","1010","1010","0011"},
//											  {"1001","1010","0110","0111","1000","1011","0011","1001","1010","0100"},
//											  {"1101","1011","1010","1110","1011","0111","0101","0100","1010","0011"},
//											  {"0101","0101","0000","0000","0101","0101","1100","1010","1010","0111"},
//											  {"0100","1100","1010","1010","0110","1100","1010","1010","1010","0110"}};
											
	//controla por onde o individuo já esteve na matriz, a fim de penalização por voltar em célula já visitada												  
	int[][] matrizControle = new int[10][10];								 
												  
	boolean atravessouParede = false, atingiuObjetivo = false;

	//gera um indivíduo aleatório
	public Individuo(int numGenes) {
		genes = "";
		Random r = new Random();

		for(int i=0;i<52;i++) {
			if(r.nextDouble() <= 0.5) {
				genes += "1";
			}
			else {
				genes += "0";
			}
		}

		geraAptidao();        
	}

	//cria um indivíduo com os genes definidos
	public Individuo(String genes) {    
		this.genes = genes;

		Random r = new Random();
		//se for mutar, cria um gene aleatório
		if (r.nextDouble() <= Algoritmo.getTaxaDeMutacao()) {
			String geneNovo="";
			int posAleatoria = r.nextInt(genes.length());
			for (int i = 0; i < genes.length(); i++) {
				if (i == posAleatoria || i == posAleatoria+1){
					// geneNovo += caracteres.charAt(r.nextInt(caracteres.length()));
//					if(r.nextDouble() <= 0.5) {
//						geneNovo += "1";
//					}
//					else {
//						geneNovo += "0";
//					}
					if(genes.charAt(i) == '0')
						geneNovo += "1";
					else
						geneNovo += "0";
				}else{
					geneNovo += genes.charAt(i);
				}

			}
			this.genes = geneNovo;   
		}
		geraAptidao();
	}

	public void geraAptidao() {
		int i = 9, j = 0; //começa no início do tabuleiro (posição [9][0]);
		String gene = new String("");
		
		for(int l=0;l<10;l++){
			for(int c=0;c<10;c++)
				matrizControle[l][c] = 0;
		}
				
		boolean saiuLabirinto = false;
		atravessouParede = false;
		atingiuObjetivo = false;
		aptidao = 0;
		for(int k = 0;k < 52;k += 2) { //percore os 52 bits
			gene = genes.substring(k, k+2); // gene recebe um conjunto de 2 bits

			switch (gene){
			case "00": // Leste									
				if(labirinto[i][j].substring(0, 1).equals("1")) {
					aptidao += 20;
				} else {
					aptidao -= 22;
					atravessouParede = true;
				}
				
				if(matrizControle[i][j] == 1) { //penaliza se já esteve naquela célula
					aptidao -= 15;
				} else {
					matrizControle[i][j] = 1;
				}					
				
				j++;
				
				if(j > 9) {
					aptidao -= 100;
					saiuLabirinto = true;
					break;
				} else if(j < 0){
					aptidao -= 100;
					saiuLabirinto = true;
					break;
				//se atingiu o objetivo
				} else if(i == 0 && j == 9) {
					atingiuObjetivo = true;
					aptidao += 5;
				}
				break;
			case "01": // Norte				
				if(labirinto[i][j].substring(1, 2).equals("1")) {
					aptidao += 22;
				}else {
					aptidao -= 22;
					atravessouParede = true;
				}
				
				if(matrizControle[i][j] == 1) { //penaliza se já esteve naquela célula
					aptidao -= 15;
				} else {
					matrizControle[i][j] = 1;
				}			
				
				i--;
				
				if(i > 9) {
					aptidao -= 100;
					saiuLabirinto = true;
					break;
				} else if(i < 0){
					aptidao -= 100;
					saiuLabirinto = true;
					break;
				} else if(i == 0 && j == 9) {
					atingiuObjetivo = true;
					aptidao += 20;
				}
				break;
			case "10": // Oeste				
				if(labirinto[i][j].substring(2, 3).equals("1")) {
					aptidao += 22;
				}else {
					aptidao -= 22;
					atravessouParede = true;
				}
				
				if(matrizControle[i][j] == 1) { //penaliza se já esteve naquela célula
					aptidao -= 15;
				} else {
					matrizControle[i][j] = 1;
				}			
				
				j--;
				
				if(j > 9) {
					aptidao -= 100;
					saiuLabirinto = true;
					break;
				} else if(j < 0){
					aptidao -= 100;
					saiuLabirinto = true;
					break;
				} else if(i == 0 && j == 9) {
					atingiuObjetivo = true;
					aptidao += 20;
				}
				break;
			case "11": // Sul
				if(labirinto[i][j].substring(3, 4).equals("1")) {
					aptidao += 1;
				}else {
					aptidao -= 22;
					atravessouParede = true;
				}
				
				if(matrizControle[i][j] == 1) { //penaliza se já esteve naquela célula
					aptidao -= 15;
				} else {
					matrizControle[i][j] = 1;
				}			
				
				i++;
				
				if(i > 9) {
					aptidao -= 100;
					saiuLabirinto = true;
					break;
				} else if(i < 0){
					aptidao -= 100;
					saiuLabirinto = true;
					break;
				} else if(i == 0 && j == 9) {
					atingiuObjetivo = true;
					aptidao += 20;
				}
				
				break;
			}
			
			if(saiuLabirinto)
				break;
		}
		
		if(i == 0 && j == 9) 
			atingiuObjetivo = true;
		
		aptidao += (-i+9+(j-1)) * 10; // penalidade aplicada em relação à distância da celula objetivo 
	}

	public int getAptidao() {
		return aptidao;
	}

	public String getGenes() {
		return genes;
	}
}