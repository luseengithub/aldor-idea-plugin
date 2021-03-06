package aldor.hierarchy;

import aldor.parser.SwingThreadTestRule;
import aldor.test_util.LightPlatformJUnit4TestRule;
import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NullHierarchyTreeStructureTest {
    private final CodeInsightTestFixture codeTestFixture = LightPlatformJUnit4TestRule.createFixture(new LightProjectDescriptor());

    @Rule
    public final TestRule platformTestRule =
            RuleChain.emptyRuleChain()
                    .around(new LightPlatformJUnit4TestRule(codeTestFixture, ""))
                    .around(new SwingThreadTestRule());


    @Test
    public void testNullHierarchy() {
        String text = "List";
        PsiFile whole = codeTestFixture.addFileToProject("test.spad", text);

        NullHierarchyTreeStructure structure = new NullHierarchyTreeStructure(whole, "hamsters");

        HierarchyNodeDescriptor base = structure.getBaseDescriptor();
        base.update();
        assertTrue(base.isValid());
        System.out.println("Base: " + base);
        Object[] children = structure.getChildElements(base);
        assertEquals(1, children.length);
        NodeDescriptor<?> child = (NodeDescriptor<?>) children[0];
        child.update();
        System.out.println("Child: " + child);

    }
}
