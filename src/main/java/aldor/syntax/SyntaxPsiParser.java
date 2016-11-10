package aldor.syntax;

import aldor.psi.AldorAddPart;
import aldor.psi.AldorDeclPart;
import aldor.psi.AldorE14;
import aldor.psi.AldorId;
import aldor.psi.AldorInfixedExpr;
import aldor.psi.AldorInfixedTok;
import aldor.psi.AldorJxleftAtom;
import aldor.psi.AldorLiteral;
import aldor.psi.AldorParened;
import aldor.psi.AldorQuotedIds;
import aldor.psi.AldorRecursiveVisitor;
import aldor.psi.AldorWithPart;
import aldor.psi.JxrightElement;
import aldor.psi.NegationElement;
import aldor.syntax.components.Add;
import aldor.syntax.components.Apply;
import aldor.syntax.components.Comma;
import aldor.syntax.components.Declaration;
import aldor.syntax.components.EnumList;
import aldor.syntax.components.Id;
import aldor.syntax.components.Literal;
import aldor.syntax.components.Other;
import aldor.syntax.components.With;
import com.google.common.collect.Lists;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Turns Psi into syntax
 */
public final class SyntaxPsiParser {
    private static final Logger LOG = Logger.getInstance(SyntaxPsiParser.class);

    @Nullable
    public static Syntax parse(PsiElement elt) {
        try {
            final Deque<List<Syntax>> visitStack = new ArrayDeque<>();
            visitStack.add(Lists.newArrayList());
            elt.accept(new AldorPsiSyntaxVisitor(visitStack));
            return visitStack.getFirst().get(0);
        }
        catch (RuntimeException e) {
            LOG.error("Failed to parse " + elt.getText() + " " + elt, e);
            return null;
        }
    }

    @SuppressWarnings("OverlyCoupledClass")
    private static final class AldorPsiSyntaxVisitor extends AldorRecursiveVisitor {
        private final Deque<List<Syntax>> visitStack;

        private AldorPsiSyntaxVisitor(Deque<List<Syntax>> visitStack) {
            this.visitStack = visitStack;
        }

        @Override
        public void visitNegationElement(@NotNull NegationElement o) {
            visitStack.peek().add(new Other(o.getLastChild()));
        }

        /**
         * Scan the following:
         * E14 ::= ((E15? (WithPart | AddPart)) | (E15 (KW_Except E15 | KW_Throw E15| Nothing))) (WithPart | AddPart)*
         * Nothing to do with the canary wharf tourist board.
         */
        @Override
        public void visitE14(@NotNull AldorE14 o) {
            List<Syntax> fnOrAtom = Lists.newArrayList();
            visitStack.push(fnOrAtom);
            o.acceptChildren(this);
            visitStack.pop();
            if (fnOrAtom.size() == 1) {
                visitStack.peek().add(fnOrAtom.get(0));
            }
            else {
                // FIXME: Obviously wrong(!)
                visitStack.peek().add(new Other(o));
            }
        }

        @Override
        public void visitJxrightElement(@NotNull JxrightElement o) {
            List<Syntax> fnOrAtom = Lists.newArrayList();
            visitStack.push(fnOrAtom);
            o.acceptChildren(this);
            visitStack.pop();
            if (fnOrAtom.isEmpty()) {
                throw new IllegalStateException("Expecting something from " + o.getText());
            }

            if (fnOrAtom.size() == 1) {
                visitStack.peek().add(fnOrAtom.get(0));
            } else {
                Syntax syntax = new Apply(o, fnOrAtom);
                visitStack.peek().add(syntax);
            }
        }

        @Override
        public void visitJxleftAtom(@NotNull AldorJxleftAtom o) {
            List<Syntax> opsAndArgs = Lists.newArrayList();
            visitStack.push(opsAndArgs);
            o.acceptChildren(this);
            List<Syntax> last = visitStack.pop();
            //noinspection ObjectEquality
            assert last == opsAndArgs;

            if (opsAndArgs.isEmpty()) {
                // We're almost surely throwing something away here...
                visitStack.peek().add(new Other(o));
            }
            else if (opsAndArgs.size() == 1) {
                visitStack.peek().add(opsAndArgs.get(0));
            } else if (opsAndArgs.size() == 2) {
                visitStack.peek().add(new Apply(o, opsAndArgs));
            }
            else {
                Syntax all = opsAndArgs.get(opsAndArgs.size() - 1);
                for (Syntax syntax : Lists.reverse(opsAndArgs).subList(1, opsAndArgs.size() - 2)) {
                    all = new Apply(null, Lists.newArrayList(syntax, all));
                }
                visitStack.peek().add(all);
            }
        }

        @Override
        public void visitQuotedIds(@NotNull AldorQuotedIds ids) {
            List<Syntax> parenContent = Lists.newArrayList();
            visitStack.push(parenContent);
            ids.acceptChildren(this);
            List<Syntax>  last = visitStack.pop();
            visitStack.peek().add(new EnumList(ids, last));
        }

        @Override
        public void visitId(@NotNull AldorId o) {
            visitStack.peek().add(new Id(o));
        }

        @Override
        public void visitLiteral(@NotNull AldorLiteral o) {
            visitStack.peek().add(new Literal(o.getText(), o));
        }

        @Override
        public void visitParened(@NotNull AldorParened parened) {
            List<Syntax> parenContent = Lists.newArrayList();
            visitStack.push(parenContent);
            parened.acceptChildren(this);
            List<Syntax>  last = visitStack.pop();
            //noinspection ObjectEquality
            assert last == parenContent;
            final Syntax next;
            if (parenContent.size() == 1) {
                next = parenContent.get(0);
            } else {
                next = new Comma(parened, parenContent);
            }
            visitStack.peek().add(next);
        }

        @Override
        public void visitDeclPart(@NotNull AldorDeclPart decl) {
            List<Syntax> parenContent = Lists.newArrayList();
            visitStack.push(parenContent);
            decl.acceptChildren(this);
            List<Syntax> last = visitStack.pop();
            Syntax result = new Declaration(decl, last);
            visitStack.peek().add(result);
        }

        @Override
        public void visitWithPart(@NotNull AldorWithPart o) {
            // This isn't ideal, and we can parse the underlying stuff, but
            // leave for the moment
            visitStack.peek().add(new With(o));
        }

        @Override
        public void visitAddPart(@NotNull AldorAddPart o) {
            visitStack.peek().add(new Add(o));
        }


        @Override
        public void visitInfixedExpr(@NotNull AldorInfixedExpr expr) {
            List<Syntax> exprContent = Lists.newArrayList();
            visitStack.push(exprContent);
            expr.acceptChildren(this);
            List<Syntax>  last = visitStack.pop();
            //noinspection ObjectEquality
            assert last == exprContent;
            Syntax lhs;
            int i=1;
            if ((exprContent.size() % 2) == 0) {
                lhs = new Apply(expr, exprContent.subList(0, 2));
                i++;
            }
            else {
                lhs = exprContent.get(0);
            }

            //noinspection ForLoopWithMissingComponent
            for (; i<exprContent.size(); i+=2) {
                Syntax op = exprContent.get(i);
                lhs = new Apply(expr, Lists.newArrayList(op, lhs, exprContent.get(i+1)));
            }
            visitStack.peek().add(lhs);
        }

        @Override
        public void visitInfixedTok(@NotNull AldorInfixedTok tok) {
            visitStack.peek().add(new Id(tok));
        }
    }

    public static String prettyPrint(Syntax syntax) {
        // FIXME: Temporary
        return "{pretty: " + syntax + "}";
    }

}
