package aldor.psi.stub;

import aldor.psi.SpadAbbrev;
import com.intellij.psi.stubs.StubElement;

@SuppressWarnings("CyclicClassDependency")
public interface SpadAbbrevStub extends StubElement<SpadAbbrev> {
    AbbrevInfo info();

}
