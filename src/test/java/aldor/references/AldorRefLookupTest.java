package aldor.references;

import aldor.language.AldorLanguage;
import aldor.parser.EnsureParsingTest;
import aldor.psi.AldorE6;
import aldor.psi.AldorIdentifier;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.junit.Assert;

import static aldor.psi.AldorPsiUtils.logPsi;

public class AldorRefLookupTest extends LightPlatformCodeInsightFixtureTestCase {


    public void testReference() {
        PsiElement whole = createAldorFile("f(n: Integer): Integer == n+1");
        PsiElement theRhs = PsiTreeUtil.findChildOfType(whole, AldorE6.class);
        AldorIdentifier theRhsN = PsiTreeUtil.findChildOfType(theRhs, AldorIdentifier.class);
        Assert.assertNotNull(theRhsN);
        Assert.assertNotNull(theRhsN.getReference());
        PsiElement ref = theRhsN.getReference().resolve();
        Assert.assertNotNull(ref);
    }

    public void testLookupSingleArg() {
        String text = "f(n: Integer): Integer == n+1";
        PsiFile file = createAldorFile(text);

        PsiReference ref = file.findReferenceAt(text.indexOf("n+1"));
        Assert.assertNotNull(ref);
        PsiElement resolved = ref.resolve();
        Assert.assertNotNull(resolved);
        Assert.assertEquals(text.indexOf("n:"), resolved.getTextOffset());
    }

    public void testLookupLocal() {
        String text = "local f(n: Integer): Integer == n+1";
        PsiFile file = createAldorFile(text);
        PsiReference ref = file.findReferenceAt(text.indexOf("n+1"));
        Assert.assertNotNull(ref);
        PsiElement resolved = ref.resolve();
        Assert.assertNotNull(resolved);
        Assert.assertEquals(text.indexOf("n:"), resolved.getTextOffset());
    }

    public void testLookupDefine() {
        String text = "define f(n: Integer): Integer == n+1";
        PsiFile file = createAldorFile(text);
        PsiReference ref = file.findReferenceAt(text.indexOf("n+1"));
        Assert.assertNotNull(ref);
        PsiElement resolved = ref.resolve();
        Assert.assertNotNull(resolved);
        Assert.assertEquals(text.indexOf("n:"), resolved.getTextOffset());
    }

    public void testLookupMultiArg() {
        String text = "f(n: Integer, m: Integer): Integer == n+m";
        PsiFile file = createAldorFile(text);

        PsiReference ref = file.findReferenceAt(text.indexOf("n+m"));
        Assert.assertNotNull(ref);
        PsiElement resolved = ref.resolve();
        Assert.assertNotNull(resolved);
        Assert.assertEquals(text.indexOf("n:"), resolved.getTextOffset());
    }

    public void testLookupNegationDefinition() {
        String text = "-(x: X): Integer == -x";
        PsiFile file = createAldorFile(text);

        PsiReference ref = file.findReferenceAt(text.lastIndexOf('x'));
        Assert.assertNotNull(ref);
        PsiElement resolved = ref.resolve();
        Assert.assertNotNull(resolved);
        Assert.assertEquals(text.indexOf('x'), resolved.getTextOffset());
    }


    public void testLookupInfixArg() {
        String text = "(+)(n: Integer, m: Integer): Integer == n+m";
        PsiFile file = createAldorFile(text);
        PsiReference ref = file.findReferenceAt(text.indexOf("n+m"));
        Assert.assertNotNull(ref);
        PsiElement resolved = ref.resolve();
        Assert.assertNotNull(resolved);
        Assert.assertEquals(text.indexOf("n:"), resolved.getTextOffset());
    }



    public void testLookupNotPresent() {
        String text = "f(n: Integer): Integer == foo";
        PsiFile file = createAldorFile(text);

        PsiReference ref = file.findReferenceAt(text.indexOf("foo"));
        Assert.assertNotNull(ref);
        PsiElement resolved = ref.resolve();
        Assert.assertNull(resolved);
    }


    public void testLookupInfix2Arg() {
        String text = "(n: Integer) + (m: Integer): Integer == n+m";
        PsiFile file = createAldorFile(text);
        PsiReference ref = file.findReferenceAt(text.indexOf("n+m"));
        Assert.assertNotNull(ref);
        PsiElement resolved = ref.resolve();

        Assert.assertNotNull(resolved);
        Assert.assertEquals(text.indexOf("n:"), resolved.getTextOffset());
    }

    public void testLookupCurriedArg() {
        String text = "f(n: Integer)(m: Integer): Integer == n+m+1";
        PsiFile file = createAldorFile(text);

        PsiReference refN = file.findReferenceAt(text.indexOf("n+m"));
        Assert.assertNotNull(refN);
        PsiElement resolved = refN.resolve();
        Assert.assertNotNull(resolved);
        Assert.assertEquals(text.indexOf("n:"), resolved.getTextOffset());

        PsiReference refM = file.findReferenceAt(text.indexOf("m+1"));
        Assert.assertNotNull(refM);
        resolved = refM.resolve();
        Assert.assertNotNull(resolved);
        Assert.assertEquals(text.indexOf("m:"), resolved.getTextOffset());
    }

    public void testLookupForVar() {
        String text = "for x in 1..10 repeat foo(x)";
        PsiFile file = createAldorFile(text);
        PsiReference ref = file.findReferenceAt(text.indexOf("x)"));
        Assert.assertNotNull(ref);
        PsiElement resolved = ref.resolve();
        Assert.assertNotNull(resolved);
        Assert.assertEquals(text.indexOf("x in"), resolved.getTextOffset());
    }

    public void testLookupCollectionVar() {
        String text = "[x for x in 1..10]";
        PsiFile file = createAldorFile(text);

        PsiReference ref = file.findReferenceAt(text.indexOf('x')); // First index in this case
        Assert.assertNotNull(ref);
        PsiElement resolved = ref.resolve();
        Assert.assertNotNull(resolved);
        Assert.assertEquals(text.indexOf("x in"), resolved.getTextOffset());
    }


    public void testLookupAssignment() {
        String text = "foo(): () == { x := 1; x}";
        PsiFile file = createAldorFile(text);

        PsiReference ref = file.findReferenceAt(text.indexOf("x}"));
        logPsi(file);
        Assert.assertNotNull(ref);
        PsiElement resolved = ref.resolve();
        Assert.assertNotNull(resolved);
        Assert.assertEquals(text.indexOf("x :="), resolved.getTextOffset());
    }




    private PsiFile createAldorFile(String text) {
        return createLightFile("foo.as", AldorLanguage.INSTANCE, text);
    }

    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new EnsureParsingTest.AldorProjectDescriptor();
    }

}
