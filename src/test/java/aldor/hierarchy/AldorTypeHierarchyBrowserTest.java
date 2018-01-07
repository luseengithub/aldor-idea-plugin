package aldor.hierarchy;

import aldor.parser.SwingThreadTestRule;
import aldor.test_util.ExecutablePresentRule;
import aldor.test_util.JUnits;
import aldor.test_util.LightPlatformJUnit4TestRule;
import aldor.test_util.SdkProjectDescriptors;
import com.intellij.ide.hierarchy.HierarchyProvider;
import com.intellij.ide.hierarchy.HierarchyTreeStructure;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.intellij.ide.hierarchy.TypeHierarchyBrowserBase.SUPERTYPES_HIERARCHY_TYPE;

public class AldorTypeHierarchyBrowserTest {
    private final ExecutablePresentRule fricasExecutableRule = new ExecutablePresentRule.Fricas();
    private final CodeInsightTestFixture codeTestFixture = LightPlatformJUnit4TestRule.createFixture(getProjectDescriptor(fricasExecutableRule));

    @Rule
    public final TestRule platformTestRule =
            RuleChain.emptyRuleChain()
                    .around(new TestRule() {
                        @Override
                        public Statement apply(Statement statement, Description description) {
                            return JUnits.prePostStatement(() -> {JUnits.setLogToInfo();}, () -> checkUIState(), statement);
                        }
                    })
                    .around(fricasExecutableRule)
                    .around(new LightPlatformJUnit4TestRule(codeTestFixture, ""))
                    .around(new SwingThreadTestRule());

    private void checkUIState() {
        System.out.println("Done");
    }

    @Test
    public void testReference() {
        String text = "x: List X == empty()";
        PsiFile whole = codeTestFixture.addFileToProject("test.spad", text);

        HierarchyProvider provider = new AldorTypeHierarchyProvider();
        PsiElement elt = whole.findElementAt(text.indexOf("List"));

        DataContext context = SimpleDataContext.getSimpleContext(CommonDataKeys.PSI_ELEMENT.getName(), elt,
                SimpleDataContext.getProjectContext(codeTestFixture.getProject()));

        PsiElement target = provider.getTarget(context);
        AldorTypeHierarchyBrowser browser = (AldorTypeHierarchyBrowser) provider.createHierarchyBrowser(target);

        Assert.assertNotNull(target);

        provider.browserActivated(browser);

        System.out.println("Browser: " + browser);
        HierarchyTreeStructure hierarchy = browser.createHierarchyTreeStructure(SUPERTYPES_HIERARCHY_TYPE, target);
        Assert.assertNotNull(hierarchy);

        AldorHierarchyNodeDescriptor rootDescriptor = (AldorHierarchyNodeDescriptor) hierarchy.getRootElement();
        rootDescriptor.update();

        System.out.println("Root: " + rootDescriptor);

        System.out.println("Children: " + Arrays.stream(hierarchy.getChildElements(hierarchy.getRootElement())).peek(child -> ((NodeDescriptor<?>) child).update()).collect(Collectors.toList()));

        browser.dispose();
        ((Disposable) ProgressManager.getInstance()).dispose();
    }

    private static LightProjectDescriptor getProjectDescriptor(ExecutablePresentRule fricasExecutableRule) {
        return SdkProjectDescriptors.fricasSdkProjectDescriptor(fricasExecutableRule.prefix());

    }
}
