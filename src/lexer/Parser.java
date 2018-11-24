package lexer;

/**
 *
 * @author gustavo
 *
 * Implementacao com Modo Panico [TODO]: tratar retorno 'null' do Lexer.
 *
 *
 * Modo Pânico: para tomar a decisao de escolher uma das regras (quando mais de
 * uma disponivel), temos que olhar para o FIRST(). Caso o token da entrada nao
 * esteja no FIRST() calculado, informamos uma mensagem de erro e inicia-se o
 * Modo Panico: [1] calculamos o FOLLOW do NAO-TERMINAL (a esquerda) da regra
 * atual; [2] se o token da entrada esta neste FOLLOW, desempilha-se o
 * nao-terminal atual; [3] caso contrario, avancamos a entrada para nova
 * comparacao e mantemos o nao-terminal no topo da pilha (recursiva).
 *
 * O Modo Panico encerra-se, automagicamente, quando um token esperado (FIRST)
 * ou (FOLLOW) aparece.
 *
 *
 */
public class Parser {

    private final Lexer lexer;
    private Token token;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        token = lexer.getToken(); // Leitura inicial obrigatoria do primeiro simbolo
        //System.out.println("[DEBUG]" + token.toString());
    }

    // Fecha os arquivos de entrada e de tokens
    public void fechaArquivos() {
        lexer.fechaArquivo();
    }

    public void erroSintatico(String mensagem) {

        System.out.print("[Erro Sintatico] na linha " + token.getLinha() + " e coluna " + token.getColuna() + ": ");
        System.out.println(mensagem + "\n");
    }

    public void advance() {
        token = lexer.getToken();
        System.out.println("[DEBUG]" + token.toString());
    }

    public void skip(String mensagem) {
        erroSintatico(mensagem);
        advance();
    }

    // verifica token esperado t
    public boolean eat(Tag t) {
        if (token.getClasse() == t) {
            advance();
            return true;
        } else {
            return false;
        }
    }

    public void Programa() {
        if (token.getClasse() != Tag.KW) {
            //skip("Esperado \"Algoritmo\", encontrado " + "\"" + token.getLexema() + "\"");
        }

        if (!eat(Tag.KW_algoritmo)) {
            erroSintatico("Esperado \"Algoritmo\", encontrado " + "\"" + token.getLexema() + "\"");
        }

        if (!eat(Tag.ID)) { // espera "ID"
            erroSintatico("Esperado um \"ID\", encontrado " + "\"" + token.getLexema() + "\"");
        }

        if (token.getClasse() == Tag.KW_declare) {  // verifica se está declarando as variáveis.
            RegexDeclVar(); // declare
        }   // sem else, sem erros = ou vazio
    }

    public void RegexDeclVar() {
        if (!eat(Tag.KW_declare)) {
            erroSintatico("Esperado um \"declare\" encontrado \"" + token.getLexema() + "\"");
        }

        Tipo();
        ListaID();

        if (!eat(Tag.SMB_SEMICOLON)) {
            erroSintatico("Esperado \";\", encontrado " + "\"" + token.getLexema() + "\"");
        }

        DeclaraVar();

    }

    public void DeclaraVar() {
        if (token.getClasse() == Tag.KW_numerico || token.getClasse() == Tag.KW_literal || token.getClasse() == Tag.KW_logico) {
            Tipo();
            ListaID();

            if (!eat(Tag.SMB_SEMICOLON)) {
                erroSintatico("Esperado \";\", encontrado " + "\"" + token.getLexema() + "\"");
            }
            DeclaraVar();
        }   // se estiver chamando outro token else vazio

    }

    public void ListaRotina() {
    }

    public void ListaRotinaLinha() {
    }

    public void Rotina() {
    }

    public void ListaParam() {
    }

    public void ListaParamLinha() {
    }

    public void Param() {
    }

    public void ListaID() {
        if (!eat(Tag.ID)) { // espera "ID"
            erroSintatico("Esperado um \"ID\", encontrado " + "\"" + token.getLexema() + "\"");
        }
        ListaIDLinha();
    }

    public void ListaIDLinha() {
        if (token.getClasse() == Tag.SMB_VIRGULA) {
            if (!eat(Tag.SMB_VIRGULA)) { // espera ";"
                erroSintatico("Esperado um \";\", encontrado " + "\"" + token.getLexema() + "\"");
            }
            if (!eat(Tag.ID)) { // espera "ID"
                erroSintatico("Esperado um \"ID\", encontrado" + "\"" + token.getLexema() + "\"");
            }
        }
    }

    public void Retorno() {
    }

    public void Tipo() {
        if (token.getClasse() == Tag.KW_numerico || token.getClasse() == Tag.KW_literal || token.getClasse() == Tag.KW_logico) {
            // verifica os possiveis tipos de variaveis
            advance();
        } else {
            erroSintatico("Esperado um tipo (numerico, verdadeiro, lógico) encontrado \"" + token.getClasse() + "\"");
        }
    }
}
