package br.com.comcet.tp2;

import br.com.comcet.tp1.scanner.Token;
import br.com.comcet.tp1.scanner.TokenType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MainScanner {
    public static void main(String[] args) {
        Path caminhoArquivo = Path.of("teste.pas"); 
        
        try {
            String codigoFonte = Files.readString(caminhoArquivo);
            System.out.println("Analisando código do arquivo:\n" + codigoFonte + "\n");
            
            Scanner scanner = new Scanner(codigoFonte);
            Token token;
            
            do {
                token = scanner.nextToken();
                System.out.printf("[%s, \"%s\"]\n", token.type(), token.text());
            } while (token.type() != TokenType.EOF);

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }
}