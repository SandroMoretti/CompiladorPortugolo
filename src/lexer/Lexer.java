/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexer;

import java.io.*;

public class Lexer {

    private static final int END_OF_FILE = -1; // contante para fim do arquivo
    private static int lookahead = 0; // armazena o último caractere lido do arquivo	
    public static int n_line = 1; // contador de linhas
    public static int n_column = 0; // contador de linhas
    private RandomAccessFile instance_file; // referencia para o arquivo
    static TSS tabelaSimbolos; // tabela de simbolos
    private static boolean first = false;
    private static boolean retornou = false;
    private static String lastError = null;

    public Lexer(String input_data) {

        // Abre instance_file de input_data
        try {
            instance_file = new RandomAccessFile(input_data, "r");
        } catch (IOException e) {
            System.out.println("Erro de abertura do arquivo " + input_data + "\n" + e);
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Erro do programa ou falha da Tabela de Simbolos\n" + e);
            System.exit(2);
        }

        tabelaSimbolos = new TSS(); // tabela de simbolos
    }

    // Fecha instance_file de input_data
    public void fechaArquivo() {

        try {
            instance_file.close();
        } catch (IOException errorFile) {
            System.out.println("Erro ao fechar arquivo\n" + errorFile);
            System.exit(3);
        }
    }

    //Reporta erro para o usuário
    public void sinalizaErro(String mensagem) {
        if (lastError == null || !lastError.equals(mensagem)) {
            lastError = mensagem;
            System.out.println("[Erro Lexico]: " + mensagem + "\n");
        }
    }

    //Volta uma posição do buffer de leitura
    public void retornaPonteiro() {
        n_column--;
        try {
            // Não é necessário retornar o ponteiro em caso de Fim de Arquivo
            if (lookahead != END_OF_FILE) {
                instance_file.seek(instance_file.getFilePointer() - 1);

            }
        } catch (IOException e) {
            System.out.println("Falha ao retornar a leitura\n" + e);
            System.exit(4);
        }
    }

    /* TODO:
    //[1]   Voce devera se preocupar quando incremetar as linhas e colunas,
    //      assim como quando decrementar ou reseta-las.
    //[2]   Toda vez que voce encontrar um lexema completo, voce deve retornar
    //      um objeto new Token(Tag, "lexema", linha, coluna). Cuidado com as
    //      palavras reservadas que ja sao cadastradas na TS. Essa consulta
    //      voce devera fazer somente quando encontrar um Identificador.
    //[3]   Se o caractere lido nao casar com nenhum caractere esperado,
    //      apresentar a mensagem de erro na linha e coluna correspondente.
    
     */
    // Obtém próximo token: esse metodo simula um AFD
    public Token getToken() {

        StringBuilder lexema = new StringBuilder();
        int estado = 0;
        char c;

        while (true) {
            c = '\u0000'; // null char
            // avanca caractere ou retorna token
            try {
                // read() retorna um inteiro. -1 em caso de EOF
                lookahead = instance_file.read();

                if (lookahead != END_OF_FILE) {
                    c = (char) lookahead; // conversao int para char
                }
            } catch (IOException e) {
                System.out.println("Erro na leitura do arquivo");
                System.exit(3);
            }

            n_column++;
            if (c == '\t') {
                n_column += 2;    // cada tabulação é contada como 3 espaços em branco
            }

            /*if (!first) {
                if(c != '\n'){
                    first = true;
                }
                //System.out.println("não incrementar (" + c + ")");
            } else {
                if (c == '\n') {
                    //System.out.println("NOVA LINHA");
                    n_line++;
                    n_column = 1;
                    first = false;
                } else {
                    //System.out.println("Alg: " + ((int)c) + " ...");
                    System.out.println("Incrementando (" + c + ") == " + n_column);
                    n_column++;
                }
            }*/
            // movimentacao do automato
            if (c == '\n') {
                n_line++;
                n_column = 0;
            }
            switch (estado) {
                // estado 1
                case 0:
                    if (lookahead == END_OF_FILE) {
                        return new Token(Tag.EOF, "EOF", n_line, n_column);
                    } else if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                        // Permance no estado = 1
                        estado = 0;
                    } else if (Character.isLetter(c)) {
                        lexema.append(c);
                        estado = 5;
                    } else if (Character.isDigit(c)) {
                        lexema.append(c);
                        estado = 7;
                    } else if (c == '<') {
                        estado = 21;
                    } else if (c == '>') {
                        estado = 22;
                    } else if (c == '=') {
                        estado = 18;
                    } else if (c == ',') {
                        return new Token(Tag.SMB_VIRGULA, ",", n_line, n_column);
                    } else if (c == '/') {
                        //estado = 4;
                        estado = 4;
                        //return new Token(Tag.RELOP_DIV, "/", n_line, n_column);
                    } else if (c == '*') {
                        //estado = 4;
                        return new Token(Tag.RELOP_MULT, "*", n_line, n_column);
                    } else if (c == '+') {
                        //estado = 2;
                        return new Token(Tag.RELOP_SUM, "+", n_line, n_column);
                    } else if (c == '-') {
                        //estado = 3;
                        return new Token(Tag.RELOP_MINUS, "-", n_line, n_column);
                    } else if (c == ';') {
                        //estado = 27;
                        return new Token(Tag.SMB_SEMICOLON, ";", n_line, n_column);
                    } else if (c == '(') {
                        //estado = 28;
                        return new Token(Tag.SMB_OP, "(", n_line, n_column);
                    } else if (c == ')') {
                        //estado = 29;
                        return new Token(Tag.SMB_CP, ")", n_line, n_column);
                    } else if (c == '"') {
                        estado = 12;
                    } else {
                        sinalizaErro("Caractere invalido " + c + " na linha " + n_line + " e coluna " + n_column);
                    }
                    break;
                case 4:
                    if (c == '/') {
                        // comentário d linha
                        estado = 31;
                    } else if (c == '*') {
                        // comentário /*
                        estado = 29;
                    } else {
                        // retorna /
                        return new Token(Tag.RELOP_DIV, "/", n_line, n_column);
                    }
                    break;
                case 29:
                    if (c == '*') {
                        estado = 30;
                    }
                    break;
                case 30:
                    if (c == '/') {
                        estado = 0;
                    } else if (c == '*') {
                    } else {
                        estado = 29;
                    }
                    break;

                case 31:
                    if (c == '\n') {
                        estado = 0;
                    }
                    break;
                case 21:
                    if (c == '=') {
                        //estado = 7;
                        return new Token(Tag.RELOP_LE, "<=", n_line, n_column);
                    } else if (c == '>') {
                        return new Token(Tag.RELOP_NE, "<>", n_line, n_column);
                    }
                    if (c == '-') {
                        estado = 34;
                        break;
                    } else {
                        //estado = 8;
                        retornaPonteiro();
                        return new Token(Tag.RELOP_LT, "<", n_line, n_column);
                    }
                case 22:
                    if (c == '=') {
                        //estado = 10;
                        return new Token(Tag.RELOP_GE, ">=", n_line, n_column);
                    } else {
                        //estado = 11;
                        retornaPonteiro();
                        return new Token(Tag.RELOP_GT, ">", n_line, n_column);
                    }

                case 34:
                    if (c == '-') {
                        // estado = 35;
                        return new Token(Tag.SMB_ATRIBUICAO, "<--", n_line, n_column);
                    } else {
                        // estado = 36;
                        retornaPonteiro();
                        retornaPonteiro();
                        return new Token(Tag.RELOP_LT, "<", n_line, n_column);
                    }
                case 18:
                    if (c == '=') {
                        //estado = 13;
                        return new Token(Tag.RELOP_EQ, "==", n_line, n_column);
                    } else {
                        //estado = 14;
                        retornaPonteiro();
                        return new Token(Tag.RELOP_EQ, "=", n_line, n_column);
                    }
                case 5:
                    if (Character.isLetterOrDigit(c)) {
                        lexema.append(c);
                        // Permanece no estado 5
                    } else {
                        //estado = 6;
                        retornaPonteiro();
                        Token token = tabelaSimbolos.retornaToken(lexema.toString()); // consulta TS
                        if (token == null) {
                            return new Token(Tag.ID, lexema.toString(), n_line, n_column);
                        } else {
                            token.setLinha(n_line);
                            token.setColuna(n_column);
                        }
                        return token;
                    }
                    break;
                case 7:
                    if (Character.isDigit(c)) {
                        lexema.append(c);
                        // Permanece no estado 7
                    } else if (c == '.') {
                        lexema.append(c);
                        estado = 8;
                    } else {
                        //estado = 9;
                        retornaPonteiro();
                        return new Token(Tag.NUMERICO, lexema.toString(), n_line, n_column);
                    }
                    break;
                case 8:
                    if (Character.isDigit(c)) {
                        lexema.append(c);
                        estado = 10;
                    } else {
                        retornaPonteiro();
                        sinalizaErro("Padrao para [NUMERICO] invalido na linha " + n_line + " coluna " + n_column);
                    }
                    break;
                case 10:
                    if (Character.isDigit(c)) {
                        lexema.append(c);
                        // permanece no estado 10
                    } else {
                        //estado = 11;
                        retornaPonteiro();
                        return new Token(Tag.NUMERICO, lexema.toString(), n_line, n_column); // double == numerico (?)
                    }
                    break; //\\    """a" => "a"
                case 12:
                    if (c == '"') {
                        sinalizaErro("String deve conter pelo menos um caractere. Erro na linha " + n_line + " coluna " + n_column);
                    } else if (c == '\n') {
                        sinalizaErro("Padrao para [LITERAL] invalido na linha " + n_line + " coluna " + n_column);
                    } else if (lookahead == END_OF_FILE) {
                        sinalizaErro("String deve ser fechada com \" antes do fim de arquivo");
                        return null;    // não conseguiu recuperar-se do erro ~~ causa loop infinito não retornar null aqui.
                    } else {
                        lexema.append(c);
                        estado = 13;
                    }
                    break;
                case 13:
                    if (c == '"') {
                        //estado = 14;
                        return new Token(Tag.LITERAL, lexema.toString(), n_line, n_column);
                    } else if (c == '\n') {
                        sinalizaErro("Padrao para [LITERAL] invalido na linha " + n_line + " coluna " + n_column);
                    } else if (lookahead == END_OF_FILE) {
                        sinalizaErro("String deve ser fechada com \" antes do fim de arquivo");
                        return null;    // não conseguiu recuperar-se do erro ~~ causa loop infinito não retornar null aqui.
                    } else {
                        lexema.append(c);
                        // permanece no estado 13
                    }
                    break;
            } // fim switch
        } // fim while
    } // fim proxToken()

//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String[] args) {
//        Lexer lexer = new Lexer("C:\\projetos\\tp_lexer\\Lexer\\portugolo.txt");
//        Token token;
//        tabelaSimbolos = new TSS();
//
//        // Enquanto nao houver erros ou nao for fim de arquivo:
//        do {
//            token = lexer.getToken();
//
//            // Imprime token
//            if (token != null) {
//                //System.out.println("antes:"  + token.getColuna() + " ... " + token.getLexema().length());
//                if (token.getClasse() != Tag.EOF && lastError == null) {
//                    token.setColuna(token.getColuna() - token.getLexema().split("\n")[0].length() + 1);
//                }
//                if (token.getClasse() == Tag.LITERAL) {
//                    // tramento de aspas para coluna
//                    token.setColuna(token.getColuna() - 2);
//                }
//                lastError = null;
//
//                System.out.println("Token: " + token.toString() + "\t Linha: "
//                        + token.getLinha() + "\t Coluna: " + token.getColuna());
//                token.setLexema(token.getLexema().toLowerCase());
//                if (tabelaSimbolos.retornaToken(token.getLexema()) == null
//                        && token.getClasse() == Tag.ID) {
//                    tabelaSimbolos.put(token.getLexema(), token);
//                }
//            }
//
//        } while (token != null && token.getClasse() != Tag.EOF);
//
//        lexer.fechaArquivo();
//
//        // Imprime a tabela de simbolos
//        System.out.println("");
//        System.out.println("Tabela de simbolos:");
//        System.out.println(tabelaSimbolos.toString());
//    }
    
    
    
    public void printTS() {
        System.out.println("");
        System.out.println("--------Tabela de Simbolos--------");
        System.out.println(tabelaSimbolos.toString());
        System.out.println();
    }
}
