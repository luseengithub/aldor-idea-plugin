package aldor.syntax.components;

import aldor.lexer.AldorTokenType;
import aldor.lexer.AldorTokenTypes;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public abstract class SyntaxRepresentation<T extends PsiElement> {

    public static <T extends PsiElement> SyntaxRepresentation<T> create(T element) {
        return new PsiSyntaxRepresentation<T>(element);
    }

    @Nullable
    public abstract T element();

    public abstract AldorTokenType tokenType();

    public abstract String text();

    private static class PsiSyntaxRepresentation<T extends PsiElement> extends SyntaxRepresentation<T> {
        private final T element;

        public PsiSyntaxRepresentation(T element) {
            this.element = element;
        }

        @Override
        public T element() {
            return element;
        }

        @Override
        public AldorTokenType tokenType() {
            return AldorTokenTypes.forText(element.getText());
        }

        @Override
        public String text() {
            return element.getText();
        }
    }

}