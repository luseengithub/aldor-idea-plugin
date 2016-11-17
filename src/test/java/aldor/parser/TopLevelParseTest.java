package aldor.parser;

import aldor.psi.elements.AldorTypes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.testFramework.LightPlatformCodeInsightTestCase;

import java.util.List;

public class TopLevelParseTest extends LightPlatformCodeInsightTestCase {

    public void testTopLevelStd() {
        PsiElement psi = parseText("Foo: with == add\nQQQ: Category == with\n");
        final List<PsiErrorElement> errors = ParserFunctions.getPsiErrorElements(psi);
        assertEquals(0, errors.size());
    }

    private PsiElement parseText(CharSequence text) {
        return parseText(text, AldorTypes.TOP_LEVEL);
    }

    private PsiElement parseText(CharSequence text, IElementType elementType) {
        return ParserFunctions.parseAldorText(getProject(), text, elementType);
    }


}
