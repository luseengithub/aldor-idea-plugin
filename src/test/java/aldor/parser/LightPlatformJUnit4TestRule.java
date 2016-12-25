package aldor.parser;

import com.intellij.openapi.project.Project;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import com.intellij.testFramework.fixtures.impl.LightTempDirTestFixtureImpl;
import org.jetbrains.annotations.NonNls;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.File;

public class LightPlatformJUnit4TestRule implements TestRule {
    private final String basePath;
    private CodeInsightTestFixture myFixture = null;

    LightPlatformJUnit4TestRule(CodeInsightTestFixture fixture, String basePath) {
        this.myFixture = fixture;
        this.basePath = basePath;
    }

    public void setUp() throws Exception {
        myFixture.setUp();
        myFixture.setTestDataPath(getTestDataPath());
    }

    @NonNls
    protected String getTestDataPath() {
        String path = PlatformTestUtil.getCommunityPath();
        //noinspection StringConcatenationMissingWhitespace
        return path.replace(File.separatorChar, '/') + basePath;
    }

    @Override
    public Statement apply(Statement statement, Description description) {
        //noinspection ReturnOfInnerClass
        return new FixtureStatement(statement);
    }

    private void tearDown() throws Exception {
        myFixture.tearDown();
    }

    public Project getProject() {
        return myFixture.getProject();
    }

    public static CodeInsightTestFixture createFixture(LightProjectDescriptor descriptor) {
        IdeaTestFixtureFactory factory = IdeaTestFixtureFactory.getFixtureFactory();
        TestFixtureBuilder<IdeaProjectTestFixture> fixtureBuilder = factory.createLightFixtureBuilder(descriptor);
        final IdeaProjectTestFixture fixture = fixtureBuilder.getFixture();
        return IdeaTestFixtureFactory.getFixtureFactory().createCodeInsightFixture(fixture, new LightTempDirTestFixtureImpl(true));
    }

    private final class FixtureStatement extends Statement {
        private final Statement statement;

        private FixtureStatement(Statement statement) {
            this.statement = statement;
        }

        @Override
        public void evaluate() throws Throwable {
            setUp();
            try {
                statement.evaluate();
            }
            finally {
                tearDown();
            }
        }
    }
}
