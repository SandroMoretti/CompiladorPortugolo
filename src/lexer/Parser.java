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
        //System.out.println("[DEBUG]" + token.toString());
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
        System.out.println(token.getLexema() + ": " + token.getClasse());
        if (token.getClasse() != Tag.KW) {
            //skip("Esperado \"Algoritmo\", encontrado " + "\"" + token.getLexema() + "\"");
        }

        if (!eat(Tag.KW_algoritmo)) {
            erroSintatico("Esperado \"Algoritmo\", encontrado " + "\"" + token.getLexema() + "\"");
        }

        if (!eat(Tag.ID)) {
            erroSintatico("Esperado um \"ID\", encontrado " + "\"" + token.getLexema() + "\"");
        }
    }
}
