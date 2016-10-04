package aldor.syntax;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Core representation of Syntax.. We don't use PSI as it's probably too heavyweight,
 * and somewhat subject to change.
 */
@SuppressWarnings("AbstractClassNamingConvention")
public abstract class Syntax {
    public abstract String name();

    public abstract PsiElement psiElement();

    public abstract Iterable<Syntax> children();

    @Nullable
    public <T extends Syntax> T as(@NotNull Class<T> clzz) {
        if (clzz.isAssignableFrom(this.getClass())) {
            return clzz.cast(this);
        }
        return null;
    }

    public <T extends Syntax> boolean is(@NotNull Class<T> clzz) {
        return clzz.isAssignableFrom(this.getClass());
    }
}
