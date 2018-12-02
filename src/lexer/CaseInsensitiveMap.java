package lexer;

import java.util.HashMap;

public class CaseInsensitiveMap extends HashMap<String, Token> {

    @Override
    public Token put(String key, Token value) {
        return super.put(key.toLowerCase(), value);
    }

    // not @Override because that would require the key parameter to be of type Object
    public Token get(Token key) {
        return super.get(key);
    }
}
