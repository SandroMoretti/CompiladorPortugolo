/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexer;

import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;

/**
 *
 * @author gustavo
 */
public class TSS {

    private Map<String, Token> tabelaSimbolos; // Tabela de símbolos do ambiente

    public TSS() {
        tabelaSimbolos = new TreeMap<String, Token>(String.CASE_INSENSITIVE_ORDER);

        // Inserindo as palavras reservadas
        Token word;
        word = new Token(Tag.KW_algoritmo, "algoritmo", 0, 0);
        this.tabelaSimbolos.put("algoritmo", word);

        word = new Token(Tag.KW_declare, "declare", 0, 0);
        this.tabelaSimbolos.put("declare", word);

        word = new Token(Tag.KW_fim, "fim", 0, 0);
        this.tabelaSimbolos.put("fim", word);

        word = new Token(Tag.KW_subrotina, "subrotina", 0, 0);
        this.tabelaSimbolos.put("subrotina", word);

        word = new Token(Tag.KW_retorne, "retorne", 0, 0);
        this.tabelaSimbolos.put("retorne", word);

        word = new Token(Tag.KW_logico, "logico", 0, 0);
        this.tabelaSimbolos.put("logico", word);

        word = new Token(Tag.KW_numerico, "numerico", 0, 0);
        this.tabelaSimbolos.put("numerico", word);

        word = new Token(Tag.KW_literal, "literal", 0, 0);
        this.tabelaSimbolos.put("literal", word);

        word = new Token(Tag.KW_nulo, "nulo", 0, 0);
        this.tabelaSimbolos.put("nulo", word);

        word = new Token(Tag.KW_se, "se", 0, 0);
        this.tabelaSimbolos.put("se", word);

        word = new Token(Tag.KW_inicio, "inicio", 0, 0);
        this.tabelaSimbolos.put("inicio", word);

        word = new Token(Tag.KW_senao, "senao", 0, 0);
        this.tabelaSimbolos.put("senao", word);

        word = new Token(Tag.KW_enquanto, "enquanto", 0, 0);
        this.tabelaSimbolos.put("enquanto", word);

        word = new Token(Tag.KW_faca, "faca", 0, 0);
        this.tabelaSimbolos.put("faca", word);
        word = new Token(Tag.KW_para, "para", 0, 0);
        this.tabelaSimbolos.put("para", word);

        word = new Token(Tag.KW_ate, "ate", 0, 0);
        this.tabelaSimbolos.put("ate", word);

        word = new Token(Tag.KW_repita, "repita", 0, 0);
        this.tabelaSimbolos.put("repita", word);

        word = new Token(Tag.KW_escreva, "escreva", 0, 0);
        this.tabelaSimbolos.put("escreva", word);

        word = new Token(Tag.KW_leia, "leia", 0, 0);
        this.tabelaSimbolos.put("leia", word);

        word = new Token(Tag.KW_ou, "Ou", 0, 0);
        this.tabelaSimbolos.put("Ou", word);

        word = new Token(Tag.KW_e, "E", 0, 0);
        this.tabelaSimbolos.put("E", word);

        word = new Token(Tag.KW_nao, "Nao", 0, 0);
        this.tabelaSimbolos.put("Nao", word);
    }

    public void put(String s, Token w) {
        tabelaSimbolos.put(s, w);
    }

    // Pesquisa na tabela de símbolos se há algum token com determinado lexema
    // vamos usar esse metodo somente para diferenciar ID e KW
    public Token retornaToken(String lexema) {
        Token token = tabelaSimbolos.get(lexema);
        return token;
    }

    @Override
    public String toString() {
        String saida = "";
        int i = 1;

        for (Map.Entry<String, Token> entry : tabelaSimbolos.entrySet()) {
            saida += ("posicao " + i + ": \t" + entry.getValue().toString()) + "\n";
            i++;
        }
        return saida;
    }
}
