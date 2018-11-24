/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexer;

/**
 *
 * @author gustavo
 */
public enum Tag {

    // fim de arquivo
    EOF,
    //Operadores
    RELOP_LT, // <
    RELOP_LE, // <=
    RELOP_GT, // >
    RELOP_GE, // >=
    RELOP_EQ, // ==
    RELOP_NE, // <>
    RELOP_ASSIGN, // =
    RELOP_SUM, // +
    RELOP_MINUS, // -
    RELOP_MULT, // *
    RELOP_DIV, // /

    //Simbolos
    SMB_OP, // (
    SMB_CP, // )
    SMB_SEMICOLON, // ;
    SMB_VIRGULA, // ,
    SMB_ATRIBUICAO, // <--

    //identificador
    ID,
    //numeros
    NUMERICO,
    //DOUBLE,
    //strings
    LITERAL,
    LOGICO,
    // palavras reservada
    KW,
    KW_algoritmo,
    KW_declare,
    KW_fim,
    KW_subrotina,
    KW_retorne,
    KW_logico,
    KW_numerico,
    KW_literal,
    KW_nulo,
    KW_se,
    KW_inicio,
    KW_enquanto,
    KW_faca,
    KW_para,
    KW_ate,
    KW_repita,
    KW_escreva,
    KW_leia,
    KW_ou,
    KW_e,
    KW_nao,
    KW_senao,
    KW_verdadeiro,
    KW_falso;
}
