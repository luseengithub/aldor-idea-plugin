package aldor.parser;

import aldor.psi.elements.AldorTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

import java.io.File;
import java.util.List;

import static aldor.psi.AldorPsiUtils.logPsi;
import static aldor.test_util.TestFiles.existingFile;

public class EnsureParsing4Test {

    private final CodeInsightTestFixture testFixture = LightPlatformJUnit4TestRule.createFixture(null);

    @Rule
    public final TestRule platformTestRule =
            RuleChain.emptyRuleChain()
                    .around(new LightPlatformJUnit4TestRule(testFixture, ""))
                    .around(new SwingThreadTestRule());

    @Test
    public void testOne() {
        String text = "X: with == add";
        PsiElement psi = parseText(text);
        logPsi(psi);
        final List<PsiErrorElement> errors = ParserFunctions.getPsiErrorElements(psi);
        Assert.assertEquals(0, errors.size());
    }


    @Test
    public void testParseLang() {
        Assert.assertNotNull(getProject());

        Project project = getProject();
        File file = existingFile("/home/pab/Work/aldorgit/aldor/aldor/lib/aldor/src/lang/sal_lang.as");
        final List<PsiErrorElement> errors = parseFile(project, file);
        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void testParseITools() {
        Assert.assertNotNull(getProject());

        Project project = getProject();
        File file = existingFile("/home/pab/Work/aldorgit/aldor/aldor/lib/aldor/src/arith/sal_itools.as");
        final List<PsiErrorElement> errors = parseFile(project, file);
        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void testParseSalSSet() {
        Assert.assertNotNull(getProject());

        Project project = getProject();
        File file = existingFile("/home/pab/Work/aldorgit/aldor/aldor/lib/aldor/src/datastruc/sal_sset.as");
        final List<PsiErrorElement> errors = parseFile(project, file);
        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void testParseFold() {
        Assert.assertNotNull(getProject());

        Project project = getProject();
        File file = existingFile("/home/pab/Work/aldorgit/aldor/aldor/lib/aldor/src/datastruc/sal_fold.as");
        final List<PsiErrorElement> errors = parseFile(project, file);
        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void testParseBSearch() {
        Assert.assertNotNull(getProject());

        Project project = getProject();
        File file = existingFile("/home/pab/Work/aldorgit/aldor/aldor/lib/aldor/src/arith/sal_bsearch.as");
        final List<PsiErrorElement> errors = parseFile(project, file);
        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void testParseUPMod() {
        Assert.assertNotNull(getProject());

        Project project = getProject();
        File file = existingFile("/home/pab/Work/aldorgit/aldor/aldor/lib/algebra/src/algext/sit_upmod.as");
        final List<PsiErrorElement> errors = parseFile(project, file);
        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void testParseSExpr() {
        Assert.assertNotNull(getProject());

        Project project = getProject();
        File file = existingFile("/home/pab/Work/aldorgit/aldor/aldor/lib/aldor/src/lisp/sal_sexpr.as");
        final List<PsiErrorElement> errors = parseFile(project, file);
        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void testParseAxiomPrime() {
        Assert.assertNotNull(getProject());

        Project project = getProject();
        File file = existingFile("/home/pab/Work/aldorgit/aldor/aldor/lib/algebra/src/categories/sit_axiomprime.as");
        final List<PsiErrorElement> errors = parseFile(project, file);
        Assert.assertEquals(0, errors.size());
    }

    private Project getProject() {
        return this.testFixture.getProject();
    }

    @NotNull
    private List<PsiErrorElement> parseFile(Project project, File file) {
        Assert.assertTrue(file.exists());
        VirtualFile vf = LocalFileSystem.getInstance().findFileByIoFile(file);
        Assert.assertNotNull(vf);
        PsiFile psiFile = PsiManager.getInstance(project).findFile(vf);
        //noinspection ConstantConditions
        String text = psiFile.getText();

        PsiElement psi = parseText(text);
        logPsi(psi);
        return ParserFunctions.getPsiErrorElements(psi);
    }


    private PsiElement parseText(CharSequence text) {
        return ParserFunctions.parseAldorText(testFixture.getProject(), text, AldorTypes.TOP_LEVEL);
    }

}
