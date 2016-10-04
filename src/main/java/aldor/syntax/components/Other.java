package aldor.syntax.components;

import aldor.syntax.Syntax;
import com.intellij.psi.PsiElement;

import java.util.Collections;

/**
 * Created by pab on 04/10/16.
 */
public class Other extends Syntax {
    private final PsiElement other;

    public Other(PsiElement other) {
        this.other = other;
    }

    @Override
    public String name() {
        return "Other";
    }

    @Override
    public PsiElement psiElement() {
        return other;
    }

    @Override
    public Iterable<Syntax> children() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "(" + name() + ")";
    }

}
