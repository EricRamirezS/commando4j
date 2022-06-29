import edu.rice.cs.util.ArgumentTokenizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ParseArgsTest {

    @Test
    public void Tokenizer() {
        String input = "paraA paramB \"long text paramC\" paramD param\\\"e";
        List<String> args = ArgumentTokenizer.tokenize(input);

        Assertions.assertEquals(5, args.size());

        Assertions.assertEquals("paraA", args.get(0));
        Assertions.assertEquals("paramB", args.get(1));
        Assertions.assertEquals("long text paramC", args.get(2));
        Assertions.assertEquals("paramD", args.get(3));
        Assertions.assertEquals("param\"e", args.get(4));
    }

    @Test
    public void TokenizerFixedArgsNumber() {
        String input = "paraA paramB \"long text paramC\" paramD param\\\"e";
        List<String> args = ArgumentTokenizer.tokenize(input, 4);

        Assertions.assertEquals(4, args.size());

        Assertions.assertEquals("paraA", args.get(0));
        Assertions.assertEquals("paramB", args.get(1));
        Assertions.assertEquals("long text paramC", args.get(2));
        Assertions.assertEquals("paramD param\"e", args.get(3));
    }
}
