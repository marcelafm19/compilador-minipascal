package br.com.comcet.tp0;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {
        String input = "";
        boolean isFilePath = false;

        // 1. Prioridade: Argumentos de Linha de Comando (CLI)
        if (args.length > 0) {
            if (args[0].equals("-f") && args.length > 1) {
                input = args[1];
                isFilePath = true;
            } else {
                input = args[0];
                // Detecção automática simples se não houver flag
                if (input.toLowerCase().endsWith(".txt") && new File(input).exists()) {
                    isFilePath = true;
                }
            }
        } 
        // 2. Fallback: Entrada Interativa (Scanner)
        else {
            Scanner keyboard = new Scanner(System.in);
            System.out.print("\nDigite o texto ou o caminho de um arquivo .txt: ");
            input = keyboard.nextLine();
            if (input.toLowerCase().endsWith(".txt") && new File(input).exists()) {
                isFilePath = true;
            }
            keyboard.close();
        }

        String content = "";
        if (isFilePath) {
            try {
                content = new Scanner(new File(input)).useDelimiter("\\Z").next();
            } catch (FileNotFoundException e) {
                System.err.println("Erro: Arquivo não encontrado: " + input);
                return;
            }
        } else {
            content = input;
        }

        exibirEstatisticas(content);
    }

    public static void exibirEstatisticas(String text) {
        // Validação inicial cobrada nos testes (Texto vazio)
        if (text == null || text.trim().isEmpty()) {
            System.out.println("\nTexto vazio.");
            return;
        }

        // 1. Limpar o texto (converter para minúsculas)
        String textoMinusculo = text.toLowerCase();

        // 2. Contar caracteres (apenas letras a-z)
        long totalCaracteres = textoMinusculo.chars()
                .filter(c -> c >= 'a' && c <= 'z')
                .count();

        // 3. Extrair palavras (separar por qualquer caractere que não seja letra a-z)
        List<String> palavras = Arrays.stream(textoMinusculo.split("[^a-z]+"))
                .filter(w -> !w.isEmpty())
                .collect(Collectors.toList());

        int totalPalavras = palavras.size();

        if (totalPalavras == 0) {
            System.out.println("\nTexto vazio.");
            return;
        }

        // 4. Encontrar letra mais frequente (critério: presente no maior nº de palavras)
        Map<Character, Integer> frequenciaLetras = new HashMap<>();
        for (String palavra : palavras) {
            Set<Character> letrasUnicasNaPalavra = new HashSet<>();
            for (char c : palavra.toCharArray()) {
                letrasUnicasNaPalavra.add(c);
            }
            for (char c : letrasUnicasNaPalavra) {
                frequenciaLetras.put(c, frequenciaLetras.getOrDefault(c, 0) + 1);
            }
        }

        char letraMaisFrequente = 'z'; 
        int maxLetraCount = -1;
        for (Map.Entry<Character, Integer> entry : frequenciaLetras.entrySet()) {
            char c = entry.getKey();
            int count = entry.getValue();
            // Verifica o maior número, e em caso de empate, usa a ordem alfabética
            if (count > maxLetraCount) {
                maxLetraCount = count;
                letraMaisFrequente = c;
            } else if (count == maxLetraCount && c < letraMaisFrequente) {
                letraMaisFrequente = c; 
            }
        }

        // 5. Encontrar palavra mais frequente
        Map<String, Integer> frequenciaPalavras = new HashMap<>();
        for (String palavra : palavras) {
            frequenciaPalavras.put(palavra, frequenciaPalavras.getOrDefault(palavra, 0) + 1);
        }

        String palavraMaisFrequente = "";
        int maxPalavraCount = -1;
        for (Map.Entry<String, Integer> entry : frequenciaPalavras.entrySet()) {
            String p = entry.getKey();
            int count = entry.getValue();
            // Verifica a palavra que mais aparece, com desempate alfabético
            if (count > maxPalavraCount) {
                maxPalavraCount = count;
                palavraMaisFrequente = p;
            } else if (count == maxPalavraCount && (palavraMaisFrequente.isEmpty() || p.compareTo(palavraMaisFrequente) < 0)) {
                palavraMaisFrequente = p; 
            }
        }

        System.out.println("\nCaracteres: " + totalCaracteres);
        System.out.println("\nFrequente: " + letraMaisFrequente);
        System.out.println("\nPalavras: " + totalPalavras);
        System.out.println("\nFrequente: " + palavraMaisFrequente + "\n");
    }
}