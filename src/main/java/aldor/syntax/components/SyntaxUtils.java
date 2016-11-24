package aldor.syntax.components;


import aldor.syntax.Syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class SyntaxUtils {

    public static Iterable<Syntax> childScopesForDefineLhs(Syntax syntax) {
        if ((syntax == null) || !syntax.is(Declare.class)) {
            return Collections.singleton(syntax);
        }
        Declare decl = syntax.as(Declare.class);
        Collection<Syntax> scopes = new ArrayList<>();
        if (decl.lhs().is(Apply.class)) {
            Apply apply = decl.lhs().as(Apply.class);
            List<Syntax> ll = apply.arguments();
            for (Syntax child : ll) {
                if (child.is(Comma.class)) {
                    for (Syntax commaElt: child.children()) {
                        scopes.add(commaElt);
                    }
                }
                else if (child.is(Declare.class)){
                    scopes.add(child);
                }

            }
        }
        return scopes;
    }

}
