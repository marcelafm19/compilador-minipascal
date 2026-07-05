package br.com.comcet.tp6;

import br.com.comcet.tp1.ast.AstNode;
import br.com.comcet.tp4.MyVisitor;
import br.com.comcet.tp4.parser.MiniPascalLexer;
import br.com.comcet.tp4.parser.MiniPascalParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TypeCheckerTest {

    private AstNode parse(String codigo) {
        CharStream input = CharStreams.fromString(codigo);
        MiniPascalLexer lexer = new MiniPascalLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MiniPascalParser parser = new MiniPascalParser(tokens);
        ParseTree tree = parser.program();
        return new MyVisitor().visit(tree);
    }

    // 1. Atribuição inválida (int := string)
    @Test
    void falhaAoAtribuirStringParaInt() {
        String codigo = "program p; var x: integer; begin x := 'ola'; end.";
        AstNode ast = parse(codigo);

        TypeChecker tc = new TypeChecker();
        tc.check(ast);

        assertTrue(tc.hasErrors());
        assertTrue(tc.getErrors().stream().anyMatch(e -> e.contains("Atribuição")));
    }

    // 2. Operação aritmética com booleanos (10 + true)
    @Test
    void falhaAoSomarIntComBoolean() {
        String codigo = "program p; var x: integer; begin x := 10 + true; end.";
        AstNode ast = parse(codigo);

        TypeChecker tc = new TypeChecker();
        tc.check(ast);

        assertTrue(tc.hasErrors());
        assertTrue(tc.getErrors().stream().anyMatch(e -> e.contains("Operação '+'")));
    }

    // 3. Condição de if numérica (if 10 then ...)
    @Test
    void falhaAoUsarInteiroComoCondicaoIf() {
        String codigo = "program p; begin if 10 then writeln(1); end.";
        AstNode ast = parse(codigo);

        TypeChecker tc = new TypeChecker();
        tc.check(ast);

        assertTrue(tc.hasErrors());
        assertTrue(tc.getErrors().stream().anyMatch(e -> e.contains("booleana")));
    }

    // 4. Chamada de função passando tipos incompatíveis
    @Test
    void falhaAoPassarParametroIncorretoParaFuncao() {
        String codigo = 
                "program p; " +
                "function soma(a: integer, b: integer): integer; begin end; " +
                "begin writeln(soma(10, 'erro')); end.";
        AstNode ast = parse(codigo);

        TypeChecker tc = new TypeChecker();
        tc.check(ast);

        assertTrue(tc.hasErrors());
        // O TypeChecker deve apontar que o segundo argumento ('erro' - string) 
        // não bate com o parâmetro 'b' (integer).
    }

    // 5. Programa complexo válido passando sem erros
    @Test
    void sucessoAoValidarProgramaComplexo() {
        // Exemplo de um fatorial para testar laços, comparações lógicas e aritmética
        String codigo = 
                "program fatorial; " +
                "var n, fat: integer; " +
                "begin " +
                "   n := 5; " +
                "   fat := 1; " +
                "   while n > 0 do " +
                "   begin " +
                "       fat := fat * n; " +
                "       n := n - 1; " +
                "   end; " +
                "   writeln(fat); " +
                "end.";
        
        AstNode ast = parse(codigo);

        TypeChecker tc = new TypeChecker();
        tc.check(ast);

        // Não deve haver nenhum erro no array
        assertFalse(tc.hasErrors(), "O programa deveria ser válido, mas erros foram encontrados: " + tc.getErrors());
    }
}