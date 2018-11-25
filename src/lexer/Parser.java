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

        RegexDeclVar(); // declare
        ListaCmd();
        if (!eat(Tag.KW_fim)) {
            erroSintatico("Esperado \"fim\", encontrado " + "\"" + token.getLexema() + "\"");
        }

        if (!eat(Tag.KW_algoritmo)) {
            erroSintatico("Esperado \"algoritmo\", encontrado " + "\"" + token.getLexema() + "\"");
        }

        ListaRotina();

        if (!eat(Tag.EOF)) {
            System.out.println("Esperado fim do arquivo - encontrado " + token.getLexema());
        }
    }

    public void RegexDeclVar() {
        if (token.getClasse() != Tag.KW_declare) {  // verifica se está declarando as variáveis.
            return;
        }
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
        ListaRotinaLinha();
    }

    public void ListaRotinaLinha() {
        if (token.getClasse() == Tag.KW_subrotina) {
            Rotina();
        } else if (token.getClasse() != Tag.EOF) {
            System.out.println("Esperado fim do arquivo - encontrado " + token.getLexema());
        }
    }

    public void Rotina() {
        if (!eat(Tag.KW_subrotina)) {
            erroSintatico("Esperado \"subrotina\", encontrado " + "\"" + token.getLexema() + "\"");
        }

        if (!eat(Tag.ID)) {
            erroSintatico("Esperado \"ID\", encontrado " + "\"" + token.getLexema() + "\"");
        }

        if (!eat(Tag.SMB_OP)) {
            erroSintatico("Esperado \"(\", encontrado " + "\"" + token.getLexema() + "\"");
        }

        ListaParam();

        if (!eat(Tag.SMB_CP)) {
            erroSintatico("Esperado \")\", encontrado " + "\"" + token.getLexema() + "\"");
        }

        RegexDeclVar();

        ListaCmd();

        Retorno();

        if (!eat(Tag.KW_fim)) {
            erroSintatico("Esperado \"fim\", encontrado " + "\"" + token.getLexema() + "\"");
        }

        if (!eat(Tag.KW_subrotina)) {
            erroSintatico("Esperado \"subrotina\", encontrado " + "\"" + token.getLexema() + "\"");
        }

    }

    public void ListaParam() {
        Param();
        ListaParamLinha();
    }

    public void ListaParamLinha() {
        if (eat(Tag.SMB_VIRGULA)) {
            ListaParam();
        } else {
            if (!eat(Tag.SMB_CP)) { // follow ListaParamLinha = )
                erroSintatico("Esperado \")\", encontrado " + "\"" + token.getLexema() + "\"");
            }
        }
    }

    public void Param() {
        ListaID();
        Tipo();
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
        if (eat(Tag.KW_retorne)) {
            Expressao();
        } else if (token.getClasse() != Tag.KW_fim) {   // follow de retorno
            erroSintatico("Esperado um \"retorno\", encontrado" + "\"" + token.getLexema() + "\"");
        }
    }

    public void Tipo() {
        if (token.getClasse() == Tag.KW_numerico || token.getClasse() == Tag.KW_literal || token.getClasse() == Tag.KW_logico || token.getClasse() == Tag.KW_nulo) {
            // verifica os possiveis tipos de variaveis
            advance();
        } else {
            erroSintatico("Esperado um tipo (numerico, verdadeiro, lógico) encontrado \"" + token.getLexema() + "\"");
        }
    }

    public void ListaCmd() {
        ListaCmdLinha();
    }

    public void ListaCmdLinha() {
        switch (token.getClasse()) {
            case KW_se:
            case KW_para:
            case KW_enquanto:
            case KW_repita:
            case KW_escreva:
            case KW_leia:
            case ID:
                Cmd();
                ListaCmdLinha();
                break;
            case KW_fim:
            case KW_retorne:
            case KW_ate:
                return; // prox token dps da lsitacmd ~~ unico que não da erro caso esteja vazio ~~ representa o ε (vazio) do ListaCmdLinha
            default:    //não é um cmd e nem é vazio (indo pra fim) = erro
                skip("Esperado \"Se, Para, Enquanto, Repita, Escreva, Leia, ID\" encontrado \"" + token.getLexema() + "\"");
        }
    }

    public void Cmd() {
        switch (token.getClasse()) {
            case KW_se:
                CmdSe();
                break;
            case KW_para:
                CmdPara();
                break;
            case KW_enquanto:
                CmdEnquanto();
                break;
            case KW_repita:
                CmdRepita();
                break;
            case KW_escreva:
                CmdEscreva();
                break;
            case KW_leia:
                CmdLeia();
                break;
            case ID:
                eat(Tag.ID);
                CmdLinha();
                break;
            case KW_fim:
                return; // prox token dps da lsitacmd ~~ unico que não da erro caso esteja vazio ~~ representa o ε (vazio) do ListaCmdLinha
            default:    //não é um cmd e nem é vazio (indo pra fim) = erro
                erroSintatico("Esperado tipo CMD ou Fim, encontrado \"" + token.getLexema() + "\"");
                break;
        }
    }

    public void CmdLinha() {
        if (token.getClasse() == Tag.SMB_ATRIBUICAO) {
            CmdAtrib();
        } else if (token.getClasse() == Tag.SMB_OP) {
            CmdChamaRotina();
        } else {
            erroSintatico("Esperado \"<--\" ou \"(\", encontrado \"" + token.getLexema() + "\"");
        }
    }

    public void CmdSe() {
        //"se" "(" Expressao ")" "inicio" ListaCmd "fim" CmdSe’ 
        if (!eat(Tag.KW_se)) {
            erroSintatico("Esperado \"se\", encontrado \"" + token.getLexema() + "\"");
        }

        if (!eat(Tag.SMB_OP)) {
            erroSintatico("Esperado \"se\", encontrado \"" + token.getLexema() + "\"");
        }

        Expressao();

        if (!eat(Tag.SMB_CP)) {
            erroSintatico("Esperado \")\", encontrado \"" + token.getLexema() + "\"");
        }

        if (!eat(Tag.KW_inicio)) {
            erroSintatico("Esperado \"inicio\", encontrado \"" + token.getLexema() + "\"");
        }

        ListaCmd();

        if (!eat(Tag.KW_fim)) {
            erroSintatico("Esperado \"fim\", encontrado \"" + token.getLexema() + "\"");
        }

        CmdSeLinha();

    }

    public void CmdSeLinha() {
        switch (token.getClasse()) {
            case KW_senao:
                // first explinha
                advance();
                if (!eat(Tag.KW_inicio)) {
                    erroSintatico("Esperado \"inicio\", encontrado \"" + token.getLexema() + "\"");
                }
                ListaCmd();
                if (!eat(Tag.KW_fim)) {
                    erroSintatico("Esperado \"fim\", encontrado \"" + token.getLexema() + "\"");
                }
                break;
            case KW_fim:
            case KW_se:
            case KW_enquanto:
            case KW_para:
            case KW_repita:
            case ID:
            case KW_escreva:
            case KW_leia:
            case KW_retorne:
            case KW_ate:
                // follow - retorna vazio
                break;
            default:
                // não está no first e nem no follow - erro léxico
                erroSintatico("Esperado \"senao\" encontrado \"" + token.getLexema() + "\"");
                break;
        }
    }

    public void CmdEnquanto() {
        //"enquanto" "(" Expressao ")" "faca" "inicio" ListaCmd "fim"  
        if (!eat(Tag.KW_enquanto)) {
            erroSintatico("Esperado \"enquanto\", encontrado \"" + token.getLexema() + "\"");
        }

        if (!eat(Tag.SMB_OP)) {
            erroSintatico("Esperado \"(\", encontrado \"" + token.getLexema() + "\"");
        }

        Expressao();

        if (!eat(Tag.SMB_CP)) {
            erroSintatico("Esperado \")\", encontrado \"" + token.getLexema() + "\"");
        }

        if (!eat(Tag.KW_faca)) {
            erroSintatico("Esperado \"faca\", encontrado \"" + token.getLexema() + "\"");
        }

        if (!eat(Tag.KW_inicio)) {
            erroSintatico("Esperado \"inicio\", encontrado \"" + token.getLexema() + "\"");
        }

        ListaCmd();

        if (!eat(Tag.KW_fim)) {
            erroSintatico("Esperado \"fim\", encontrado \"" + token.getLexema() + "\"");
        }

    }

    public void CmdPara() {
        // "para" ID CmdAtrib "ate" Expressao "faca" "inicio" ListaCmd "fim"
        if (!eat(Tag.KW_para)) {
            erroSintatico("Esperado \"para\", encontrado \"" + token.getLexema() + "\"");
        }

        if (!eat(Tag.ID)) {
            erroSintatico("Esperado \"ID\", encontrado \"" + token.getLexema() + "\"");
        }

        CmdAtrib();

        if (!eat(Tag.KW_ate)) {
            erroSintatico("Esperado \"ate\", encontrado \"" + token.getLexema() + "\"");
        }

        Expressao();

        if (!eat(Tag.KW_faca)) {
            erroSintatico("Esperado \"faca\", encontrado \"" + token.getLexema() + "\"");
        }

        if (!eat(Tag.KW_inicio)) {
            erroSintatico("Esperado \"inicio\", encontrado \"" + token.getLexema() + "\"");
        }

        ListaCmd();

        if (!eat(Tag.KW_fim)) {
            erroSintatico("Esperado \"fim\", encontrado \"" + token.getLexema() + "\"");
        }
    }

    public void CmdRepita() {
        //"repita" ListaCmd "ate" Expressao
        if (!eat(Tag.KW_repita)) {
            erroSintatico("Esperado \"repita\", encontrado \"" + token.getLexema() + "\"");
        }
        ListaCmd();
        if (!eat(Tag.KW_ate)) {
            erroSintatico("Esperado \"ate\", encontrado \"" + token.getLexema() + "\"");
        }
        Expressao();
    }

    public void CmdAtrib() {
        if (!eat(Tag.SMB_ATRIBUICAO)) {
            erroSintatico("Esperado \"<--\", encontrado \"" + token.getLexema() + "\"");
        }
        Expressao();
        if (!eat(Tag.SMB_SEMICOLON)) {
            erroSintatico("Esperado \";\", encontrado \"" + token.getLexema() + "\"");
        }
    }

    public void CmdChamaRotina() {
        if (!eat(Tag.SMB_OP)) {
            erroSintatico("Esperado \"(\", encontrado \"" + token.getLexema() + "\"");
        }
        RegexExp(); // RegexExp não é nulo e ñ está errado.

        if (!eat(Tag.SMB_CP)) {
            erroSintatico("Esperado \")\", encontrado \"" + token.getLexema() + "\"");
        }
        if (!eat(Tag.SMB_SEMICOLON)) {
            erroSintatico("Esperado \";\", encontrado \"" + token.getLexema() + "\"");
        }
    }

    public void RegexExp() {
        if (token.getClasse() == Tag.ID || token.getClasse() == Tag.NUMERICO || token.getClasse() == Tag.LITERAL
                || token.getClasse() == Tag.KW_verdadeiro || token.getClasse() == Tag.KW_falso) {   // first de RegexExp
            Expressao();
            RegexExpLinha();
        } else if (token.getClasse() != Tag.SMB_CP) {   // follow de regexexp
            skip("Esperado \"ID, Numerico, Literal, Verdadeiro, Falso, )\" encontrado \"" + token.getLexema() + "\"");
        }
    }

    public void RegexExpLinha() {
        if (token.getClasse() == Tag.SMB_VIRGULA) {
            eat(Tag.SMB_VIRGULA);
            Expressao();
            RegexExpLinha();
        } else if (token.getClasse() != Tag.SMB_CP) {   // follow de regexexp
            skip("Esperado \"ID, Numerico, Literal, Verdadeiro, Falso, )\" encontrado \"" + token.getLexema() + "\"");
        }
    }

    public void CmdEscreva() {
        if (!eat(Tag.KW_escreva)) {
            erroSintatico("Esperado \"leia\" encontrado \"" + token.getLexema() + "\"");
        }
        if (!eat(Tag.SMB_OP)) {
            erroSintatico("Esperado \"(\" encontrado \"" + token.getLexema() + "\"");
        }
        Expressao();
        if (!eat(Tag.SMB_CP)) {
            erroSintatico("Esperado \")\" encontrado \"" + token.getLexema() + "\"");
        }
        if (!eat(Tag.SMB_SEMICOLON)) {
            erroSintatico("Esperado \";\" encontrado \"" + token.getLexema() + "\"");
        }
    }

    public void CmdLeia() {
        if (!eat(Tag.KW_leia)) {
            erroSintatico("Esperado \"leia\" encontrado \"" + token.getLexema() + "\"");
        }
        if (!eat(Tag.SMB_OP)) {
            erroSintatico("Esperado \"(\" encontrado \"" + token.getLexema() + "\"");
        }
        if (!eat(Tag.ID)) {
            erroSintatico("Esperado \"ID\" encontrado \"" + token.getLexema() + "\"");
        }
        if (!eat(Tag.SMB_CP)) {
            erroSintatico("Esperado \")\" encontrado \"" + token.getLexema() + "\"");
        }
        if (!eat(Tag.SMB_SEMICOLON)) {
            erroSintatico("Esperado \";\" encontrado \"" + token.getLexema() + "\"");
        }
    }

    public void Expressao() {
        Exp1();
        ExpLinha();
    }

    public void ExpLinha() {
        switch (token.getClasse()) {
            case RELOP_LT:
            case RELOP_GT:
            case RELOP_GE:
            case RELOP_LE:
            case RELOP_EQ:
            case RELOP_NE:
                // first explinha
                advance();
                Exp2();
                Exp1Linha();
                break;
            case KW_fim:
            case SMB_CP:
            case KW_faca:
            case KW_se:
            case KW_enquanto:
            case KW_para:
            case KW_repita:
            case ID:
            case KW_escreva:
            case KW_leia:
            case KW_retorne:
            case KW_ate:
            case SMB_SEMICOLON:
            case SMB_VIRGULA:
                // follow - retorna vazio
                break;
            default:
                // não está no first e nem no follow - erro léxico
                erroSintatico("Esperado \"e, ou\" encontrado \"" + token.getLexema() + "\"");
                break;
        }
    }

    public void Exp1() {
        Exp2();
        Exp1Linha();
    }

    public void Exp1Linha() {
        switch (token.getClasse()) {
            case KW_e:
            case KW_ou:
                // first exp1linha
                advance();
                Expressao();
                //Exp2();
                //Exp1Linha();
                break;
            case RELOP_LT:
            case RELOP_LE:
            case RELOP_GT:
            case RELOP_GE:
            case RELOP_EQ:
            case RELOP_NE:
            case KW_fim:
            case SMB_CP:
            case KW_faca:
            case KW_se:
            case KW_enquanto:
            case KW_para:
            case KW_repita:
            case ID:
            case KW_escreva:
            case KW_leia:
            case KW_retorne:
            case KW_ate:
            case SMB_SEMICOLON:
            case SMB_VIRGULA:
                // follow - retorna vazio
                break;
            default:
                // não está no first e nem no follow - erro léxico
                erroSintatico("Esperado \"e, ou\" encontrado \"" + token.getLexema() + "\"");
                break;
        }
    }

    public void Exp2() {
        Exp3();
        Exp2Linha();
    }

    public void Exp2Linha() {
        switch (token.getClasse()) {
            case RELOP_SUM:
            case RELOP_MINUS:
                // first exp2linha
                advance();
                Exp3();
                Exp2Linha();
                break;
            case KW_e:
            case KW_ou:
            case RELOP_LT:
            case RELOP_LE:
            case RELOP_GT:
            case RELOP_GE:
            case RELOP_EQ:
            case RELOP_NE:
            case KW_fim:
            case SMB_CP:
            case KW_faca:
            case KW_se:
            case KW_enquanto:
            case KW_para:
            case KW_repita:
            case ID:
            case KW_escreva:
            case KW_leia:
            case KW_retorne:
            case KW_ate:
            case SMB_SEMICOLON:
            case SMB_VIRGULA:
                // follow - retorna vazio
                break;
            default:
                // não está no first e nem no follow - erro léxico
                erroSintatico("Esperado \"+, -\" encontrado \"" + token.getLexema() + "\"");
                break;
        }
    }

    public void Exp3() {
        Exp4();
        Exp3Linha();
    }

    public void Exp3Linha() {
        if (eat(Tag.RELOP_MULT) || eat(Tag.RELOP_DIV)) {
            Exp4();
            Exp3Linha();
        } else if (token.getClasse() != Tag.RELOP_SUM && token.getClasse() != Tag.RELOP_MINUS && token.getClasse() != Tag.KW_e
                && token.getClasse() != Tag.KW_ou && token.getClasse() != Tag.RELOP_LT && token.getClasse() != Tag.RELOP_LE
                && token.getClasse() != Tag.RELOP_GT && token.getClasse() != Tag.RELOP_GE && token.getClasse() != Tag.RELOP_EQ
                && token.getClasse() != Tag.RELOP_NE && token.getClasse() != Tag.KW_fim && token.getClasse() != Tag.SMB_CP
                && token.getClasse() != Tag.KW_faca && token.getClasse() != Tag.KW_enquanto && token.getClasse() != Tag.KW_para
                && token.getClasse() != Tag.KW_repita && token.getClasse() != Tag.ID && token.getClasse() != Tag.KW_escreva
                && token.getClasse() != Tag.KW_leia && token.getClasse() != Tag.KW_retorne && token.getClasse() != Tag.KW_ate
                && token.getClasse() != Tag.SMB_SEMICOLON && token.getClasse() != Tag.SMB_VIRGULA) {
            // follow de Exp3Linha
            erroSintatico("Esperado \"*, /\" encontrado \"" + token.getLexema() + "\"");
        }
    }

    public void Exp4() {
        if (eat(Tag.ID)) {
            Exp4Linha();
            return;
        }
        if (token.getClasse() == Tag.NUMERICO || token.getClasse() == Tag.LITERAL || token.getClasse() == Tag.KW_verdadeiro || token.getClasse() == Tag.KW_falso) {
            advance();
            return;
        }

        if (eat(Tag.SMB_OP)) {
            Expressao();
            if (!eat(Tag.SMB_CP)) {
                erroSintatico("Esperado \")\", encontrado \"" + token.getLexema() + "\"");
            }
            return;
        }

        if (token.getClasse() == Tag.KW_nao) {
            OpUnario();
            return;
        }

        erroSintatico("Esperado \"ID, NUMERICO, LITERAL, LOGICO, (\", encontrado \"" + token.getLexema() + "\"");
    }

    public void Exp4Linha() {
        if (token.getClasse() == Tag.SMB_OP) {
            eat(Tag.SMB_OP);
            RegexExp();
            if (!eat(Tag.SMB_CP)) {
                erroSintatico("Esperado \")\", encontrado \"" + token.getLexema() + "\"");
            }
        } else if (token.getClasse() != Tag.RELOP_MULT && token.getClasse() != Tag.RELOP_DIV && token.getClasse() != Tag.RELOP_SUM
                && token.getClasse() != Tag.RELOP_MINUS && token.getClasse() != Tag.KW_e
                && token.getClasse() != Tag.KW_ou && token.getClasse() != Tag.RELOP_LT && token.getClasse() != Tag.RELOP_LE
                && token.getClasse() != Tag.RELOP_GT && token.getClasse() != Tag.RELOP_GE && token.getClasse() != Tag.RELOP_EQ
                && token.getClasse() != Tag.RELOP_NE && token.getClasse() != Tag.KW_fim && token.getClasse() != Tag.SMB_CP
                && token.getClasse() != Tag.KW_faca && token.getClasse() != Tag.KW_enquanto && token.getClasse() != Tag.KW_para
                && token.getClasse() != Tag.KW_repita && token.getClasse() != Tag.ID && token.getClasse() != Tag.KW_escreva
                && token.getClasse() != Tag.KW_leia && token.getClasse() != Tag.KW_retorne && token.getClasse() != Tag.KW_ate
                && token.getClasse() != Tag.SMB_SEMICOLON && token.getClasse() != Tag.SMB_VIRGULA) {
            // follow de Exp3Linha
            erroSintatico("Esperado \"(\" encontrado \"" + token.getLexema() + "\"");
        }
    }

    public void OpUnario() {
        if (!eat(Tag.KW_nao)) {
            erroSintatico("Esperado \"nao\", encontrado \"" + token.getLexema() + "\"");
        }
    }
}
