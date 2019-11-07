import java.io.*;
public class File {

	public static void main(String[] args) {
		try {
			FileWriter fw = new FileWriter("nuevo.txt");
			PrintWriter pw = new PrintWriter(fw);
			pw.println("Este es mi primer archivo de texto en JAVAAAAAAA");
			pw.println("Espero que no haya erroresssss");
			pw.println("Que trizteza no se escribir nada y le puses muchas ganas :(((((( ");
			pw.close();
			System.out.println("FIN");
			
		} catch(IOException ex) {
			System.out.println("No se puede escribir en el archivo.");
		}

	}

}
