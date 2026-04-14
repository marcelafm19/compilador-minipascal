package br.com.comcet.tp3;

import br.com.comcet.tp1.ast.Command;
import br.com.comcet.tp1.ast.Program;
import br.com.comcet.tp1.ast.AstNode;
import br.com.comcet.tp2.Scanner;
import java.nio.file.Files;
import java.nio.file.Path;

public class MainParser {
    public static void main(String[] args) {
        String nomeArquivo = "teste1.txt";

        try {
            String conteudo = Files.readString(Path.of(nomeArquivo));

            Scanner scanner = new Scanner(conteudo);
            Parser parser = new Parser(scanner);

            Command cmd = parser.parseCommand();

            Program ast = new Program();
            if (cmd != null) {
                ast.commands.add(cmd);
            }

            AstNode.isEtapa3 = true;
            System.out.println("AST criada com sucesso:\n");
            System.out.println(ast.printTree());

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
}