package aldor.psi;

import aldor.build.module.AldorModuleType;
import aldor.file.AldorFileType;
import com.intellij.openapi.module.ModuleType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

import java.util.Optional;

public class AldorPsiUtilsTest extends LightPlatformCodeInsightFixtureTestCase {

    public void testIsTopLevel() throws Exception {
        PsiFile file = createLightFile(AldorFileType.INSTANCE, "A: B == C");

        AldorDefine definition = PsiTreeUtil.findChildOfType(file, AldorDefine.class);
        Assert.assertNotNull(definition);
        Assert.assertTrue(AldorPsiUtils.isTopLevel(definition.getParent()));
    }

    public void testIsTopLevelWithWhere() throws Exception {
        String text = "Outer: B == C where { B ==> with { inner == b }}";
        PsiFile file = createLightFile(AldorFileType.INSTANCE, text);
        AldorDefine definition = PsiTreeUtil.findChildOfType(file, AldorDefine.class);
        Assert.assertNotNull(definition);
        Assert.assertTrue(AldorPsiUtils.isTopLevel(definition.getParent()));

        PsiElement elt = file.findElementAt(text.indexOf("inner"));
        while (!(elt instanceof AldorDefine)) {
            Assert.assertNotNull(elt);
            elt = elt.getParent();
        }
        Assert.assertFalse(AldorPsiUtils.isTopLevel(elt.getParent()));
        PsiElement elt2 = file.findElementAt(text.indexOf("inner"));
        while (!(elt2 instanceof AldorDefine)) {
            Assert.assertNotNull(elt2);
            elt2 = elt2.getParent();
        }
        Assert.assertFalse(AldorPsiUtils.isTopLevel(elt2.getParent()));
    }

    public void testDefiningFormRhsCase() {
        String text = "foo: A == with {}";
        PsiFile file = createLightFile(AldorFileType.INSTANCE, text);
        PsiElement withElt = file.findElementAt(text.indexOf("with"));
        Optional<AldorDefine> defn = AldorPsiUtils.definingForm(withElt);
        Assert.assertTrue(defn.isPresent());
    }

    public void testDefiningFormLhsCase() {
        String text = "foo: with {} == add";
        PsiFile file = createLightFile(AldorFileType.INSTANCE, text);
        PsiElement withElt = file.findElementAt(text.indexOf("with"));
        Optional<AldorDefine> defn = AldorPsiUtils.definingForm(withElt);
        Assert.assertTrue(defn.isPresent());
    }

    public void testDefiningFormMacroCase() {
        String text = "foo: E == I where { E ==> with; I ==> add}";
        PsiFile file = createLightFile(AldorFileType.INSTANCE, text);
        PsiElement withElt = file.findElementAt(text.indexOf("with"));
        Optional<AldorDefine> defn = AldorPsiUtils.definingForm(withElt);
        Assert.assertTrue(defn.isPresent());
        Assert.assertEquals(0, defn.get().getTextOffset());

        AldorIdentifier useOfE = PsiTreeUtil.findElementOfClassAtOffset(file, text.indexOf("E == "), AldorIdentifier.class, true);
        useOfE.getReference().resolve();

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
