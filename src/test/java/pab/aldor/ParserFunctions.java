package pab.aldor;

import aldor.AldorParserDefinition;
import aldor.AldorTypes;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilderFactory;
import com.intellij.lang.PsiParser;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public final class ParserFunctions {


    public static final int MAX_INDENT_DEPTH = 20;

    public static void logPsi(PsiElement psi) {
        logPsi(psi, 0);
    }

    // TODO: Remove most uses of this method
    @SuppressWarnings("SameParameterValue")
    static void logPsi(PsiElement psi, int i) {
        logPsi(psi, i, "");
    }
    static void logPsi(PsiElement psi, int depth, String lastStuff) {
        PsiElement[] children = psi.getChildren();
        int childCount = children.length;
        String text = (childCount == 0) ? psi.getText(): "";
        String spaces = Strings.repeat(" ", Math.min(depth, MAX_INDENT_DEPTH));
        if (childCount == 0) {
            System.out.println(spaces + "(psi: " + psi + " " + text + ")" + lastStuff);
            return;
        }
        System.out.println(spaces + "(psi: " + psi + " " + text);
        for (int i = 0; i < (childCount - 1); i++) {
            logPsi(children[i], depth+1, "");
        }
        logPsi(children[childCount -1], depth+1, ")" + lastStuff);
    }

    public static PsiElement parseText(Project project, CharSequence text) {
        return parseText(project, text, AldorTypes.CURLY_CONTENTS_LABELLED);
    }

    public static PsiElement parseText(Project project, CharSequence text, IElementType elementType) {
        ParserDefinition aldorParserDefinition = new AldorParserDefinition();
        PsiBuilder psiBuilder = PsiBuilderFactory.getInstance().createBuilder(aldorParserDefinition, aldorParserDefinition.createLexer(null),
                text);

        PsiParser parser = aldorParserDefinition.createParser(project);
        ASTNode parsed = parser.parse(elementType, psiBuilder);

        return parsed.getPsi();
    }


    @NotNull
    public static List<PsiErrorElement> getPsiErrorElements(PsiElement psi) {
        final List<PsiErrorElement> errors = new ArrayList<>();

        psi.accept(new PsiRecursiveElementVisitor() {

            @Override
            public void visitErrorElement(PsiErrorElement element) {
                errors.add(element);
                super.visitErrorElement(element);
            }
        });
        return errors;
    }

    public static Collection<PsiElement> find(PsiElement elt, Predicate<PsiElement> predicate) {
        List<PsiElement> subElements = Lists.newArrayList();
        elt.accept(new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (predicate.test(element)) {
                    subElements.add(element);
                }
                element.acceptChildren(this);
            }
        });
        return subElements;
    }
}
