package aldor.psi.impl;

import aldor.psi.AldorDefine;
import aldor.psi.AldorIdentifier;
import aldor.psi.stub.AldorDefineStub;
import aldor.syntax.Syntax;
import aldor.syntax.SyntaxPsiParser;
import aldor.syntax.SyntaxUtils;
import aldor.syntax.components.Apply;
import aldor.syntax.components.DeclareNode;
import aldor.syntax.components.Id;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AldorDefineMixin extends StubBasedPsiElementBase<AldorDefineStub> implements AldorDefine {
    private static final Logger LOG = Logger.getInstance(AldorDefineMixin.class);
    private static final Key<Optional<Syntax>> cachedLhsSyntax = new Key<>("LhsSyntax");

    // Must be public - parser generator insists on it.
    public AldorDefineMixin(@NotNull ASTNode node) {
        super(node);
    }

    public AldorDefineMixin(AldorDefineStub stub, IStubElementType<AldorDefineStub, AldorDefine> type) {
        super(stub, type);
    }

    @Override
    public Optional<AldorIdentifier> defineIdentifier() {
        return defineId().map(Id::aldorIdentifier);
    }

    @Override
    public DefinitionType definitionType() {
        return DefinitionType.CONSTANT;
    }

    private Optional<Id> defineId() {
        Optional<Syntax> syntaxMaybe = syntax();
        if (!syntaxMaybe.isPresent()) {
            return Optional.empty();
        }
        Syntax syntax = syntaxMaybe.get();
        if (syntax.is(DeclareNode.class)) {
            syntax = syntax.as(DeclareNode.class).lhs();
        }
        while (syntax.is(Apply.class)) {
            syntax = syntax.as(Apply.class).operator();
        }
        if (syntax.is(Id.class)) {
            return Optional.of(syntax.as(Id.class));
        }
        return Optional.empty();
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state,
                                       PsiElement lastParent, @NotNull PsiElement place) {
        PsiElement lhs = getFirstChild();

        if (!processor.execute(this, state)) {
            return false;
        }

        //noinspection ObjectEquality
        if (lastParent == lhs) {
            return true;
        }
        Optional<Syntax> syntax = syntax();

        if (syntax.isPresent()) {
            for (Syntax childScope: SyntaxUtils.childScopesForDefineLhs(syntax.get())) {
                if (!childScope.psiElement().processDeclarations(processor, state, lastParent, place)) {
                    return false;
                }
            }
        }

        return true;
    }

    @NotNull
    private Optional<Syntax> syntax() {
        Optional<Syntax> syntax = this.getUserData(cachedLhsSyntax);
        if (syntax == null) {
            PsiElement lhs = getFirstChild();
            Syntax calculatedSyntax = SyntaxPsiParser.parse(lhs);
            syntax = Optional.ofNullable(calculatedSyntax);
            this.putUserDataIfAbsent(cachedLhsSyntax, syntax);
        }
        return syntax;
    }

}
