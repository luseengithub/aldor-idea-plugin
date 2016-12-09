package aldor.psi.elements;

import aldor.language.SpadLanguage;
import aldor.psi.SpadAbbrevStubbing.SpadAbbrev;
import aldor.psi.SpadAbbrevStubbing.SpadAbbrevStub;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class SpadAbbrevElementType extends IStubElementType<SpadAbbrevStub, SpadAbbrev> {

    public static final StubIndexKey<String, SpadAbbrev> ABBREV_NAME_INDEX = StubIndexKey.createIndexKey("Spad.Abbrev.Name");
    public static final StubIndexKey<String, SpadAbbrev> ABBREV_ABBREV_INDEX = StubIndexKey.createIndexKey("Spad.Abbrev.Abbrev");

    private final PsiStubCodec<SpadAbbrevStub, SpadAbbrev> abbrevCodec;

    public SpadAbbrevElementType(AldorStubFactory stubFactory) {
        super("SpadAbbrev", SpadLanguage.INSTANCE);
        this.abbrevCodec = stubFactory.abbrevCodec();
    }

    @Override
    public SpadAbbrev createPsi(@NotNull SpadAbbrevStub stub) {
        return stub.createPsi(this);
    }

    @NotNull
    @Override
    public SpadAbbrevStub createStub(@NotNull SpadAbbrev psi, @SuppressWarnings("rawtypes") StubElement parentStub) {
        return psi.createStub(this, parentStub);
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "Spad.Abbrev";
    }

    @Override
    public void serialize(@NotNull SpadAbbrevStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        abbrevCodec.encode(stub, dataStream);
    }

    @NotNull
    @Override
    public SpadAbbrevStub deserialize(@NotNull StubInputStream dataStream, @SuppressWarnings("rawtypes") StubElement parentStub) throws IOException {
        return abbrevCodec.decode(this, dataStream, parentStub);
    }

    @Override
    public void indexStub(@NotNull SpadAbbrevStub stub, @NotNull IndexSink sink) {
        sink.occurrence(ABBREV_ABBREV_INDEX, stub.info().abbrev());
        sink.occurrence(ABBREV_NAME_INDEX, stub.info().name());
    }
}
