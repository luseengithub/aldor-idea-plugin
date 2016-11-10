package aldor.references;

import aldor.build.module.AldorModuleType;
import aldor.psi.AldorId;
import aldor.util.JUnits;
import aldor.util.SExpression;
import aldor.util.SymbolPolicy;
import aldor.util.VirtualFileTests;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;

import static aldor.symbolfile.AnnotationFileTests.lib;
import static aldor.symbolfile.AnnotationFileTests.name;
import static aldor.symbolfile.AnnotationFileTests.original;
import static aldor.symbolfile.AnnotationFileTests.srcpos;
import static aldor.symbolfile.AnnotationFileTests.syme;
import static aldor.symbolfile.AnnotationFileTests.type;
import static aldor.symbolfile.AnnotationFileTests.typeCode;
import static aldor.symbolfile.SymbolFileSymbols.Id;
import static aldor.util.SExpressions.list;
import static aldor.util.VirtualFileTests.createChildDirectory;
import static aldor.util.VirtualFileTests.createFile;

@SuppressWarnings("MagicNumber")
public class FileScopeWalkerTest extends LightPlatformCodeInsightFixtureTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        JUnits.setLogToDebug();
    }

    public void testFileReference() throws IOException {
        VirtualFile root = VirtualFileTests.getProjectRoot(getProject());
        VirtualFile srcDir = createChildDirectory(root, "src");
        VirtualFile buildDir = createChildDirectory(root, "build");
        VirtualFile markerFile = createFile(srcDir, "configure.ac", "");
        VirtualFile defFile = createFile(srcDir, "def.as", "a == 2");
        VirtualFile useFile = createFile(srcDir, "use.as", "never;\nreturn a");

        VirtualFile defAnnotationFile = createFile(buildDir, "def.abn", createDefAnnotation().toString(SymbolPolicy.ALLCAPS));
        VirtualFile useAnnotationFile = createFile(buildDir, "use.abn", createUseAnnotation().toString(SymbolPolicy.ALLCAPS));
        PsiFile file = getPsiManager().findFile(useFile);

        Collection<AldorId> ids = PsiTreeUtil.findChildrenOfType(file, AldorId.class);

        PsiElement ref = FileScopeWalker.lookupBySymbolFile(ids.iterator().next());

        assertNotNull(ref);
        assertEquals(defFile.getPath(), ref.getContainingFile().getVirtualFile().getPath());
    }

    SExpression createDefAnnotation() {
        String file = "def";
        return list(
                list(//Syntax
                        list(Id, srcpos(file, 1, 1), syme(0))
                ),
                list(//Symbols
                        list(name("a"), srcpos(file, 1, 1), type(0), typeCode(888)),
                        list(name("T"))
                ),
                list(//Types
                        list(Id, syme(1)),
                        list(Id, syme(1))));
    }

    SExpression createUseAnnotation() {
        String file = "use";
        return list(
                list(//Syntax
                        list(Id, srcpos(file, 2, "return a".length()), syme(0))
                ),
                list(//Symbols
                        list(name("a"), type(0), typeCode(888), original(1)),
                        list(name("a"), type(0), typeCode(888), lib("def.ao")),
                        list(name("T"))
                ),
                list(//Types
                        list(Id, syme(2)),
                        list(Id, syme(2))));

    }


    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        //noinspection ReturnOfInnerClass
        return new LightProjectDescriptor() {

            @Override
            @NotNull
            public ModuleType<?> getModuleType() {
                return AldorModuleType.instance();
            }

        };
    }
}
