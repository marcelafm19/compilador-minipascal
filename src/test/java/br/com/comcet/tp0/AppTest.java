package br.com.comcet.tp0;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppTest {

    @Test
    public void testEstatisticasBasicas() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        App.exibirEstatisticas("Java e legal. Java e potente.");

        String output = outContent.toString();
        assertTrue(output.contains("Caracteres: 22"));
        assertTrue(output.contains("Palavras: 6"));
        assertTrue(output.contains("Frequente: e")); // Letra e palavra 'e'
    }

    @Test
    public void testTextoDireto() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        App.exibirEstatisticas("Compiladores sao divertidos");

        String output = outContent.toString();
        assertTrue(output.contains("Caracteres: 25"));
        assertTrue(output.contains("Frequente: o"));
        assertTrue(output.contains("Palavras: 3"));
        assertTrue(output.contains("Frequente: compiladores"));
    }

    @Test
    public void testTextoVazio() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        App.exibirEstatisticas("");

        String output = outContent.toString();
        assertTrue(output.contains("Texto vazio."));
    }
}