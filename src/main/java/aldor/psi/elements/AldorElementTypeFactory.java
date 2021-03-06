package aldor.psi.elements;

import aldor.language.AldorLanguage;
import aldor.language.SpadLanguage;
import aldor.psi.AldorWhereBlock;
import aldor.psi.AldorWith;
import aldor.psi.impl.AldorBinaryWithExprImpl;
import aldor.psi.impl.AldorColonExprFixedImpl;
import aldor.psi.impl.AldorDeclPartImpl;
import aldor.psi.impl.AldorDefineMixin;
import aldor.psi.impl.AldorMacroMixin;
import aldor.psi.impl.AldorUnaryAddImpl;
import aldor.psi.impl.AldorUnaryWithExprImpl;
import aldor.psi.impl.AldorUnaryWithImpl;
import aldor.psi.impl.AldorWhereBlockImpl;
import aldor.psi.impl.AldorWithPartImpl;
import com.google.common.collect.Maps;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.stubs.PsiFileStub;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IStubFileElementType;

import java.util.Map;

import static aldor.psi.AldorDefine.DefinitionType.CONSTANT;
import static aldor.psi.AldorDefine.DefinitionType.MACRO;

/**
 * Aldor element types
 */
public class AldorElementTypeFactory {
    private static final AldorStubFactory stubFactory = new AldorStubFactoryImpl();
    public static final IElementType SPAD_ABBREV_ELEMENT_TYPE = new SpadAbbrevElementType(stubFactory.abbrevCodec());
    public static final FileStubElementType ALDOR_FILE_ELEMENT_TYPE = new FileStubElementType(AldorLanguage.INSTANCE, stubFactory.getVersion());
    public static final FileStubElementType SPAD_FILE_ELEMENT_TYPE = new FileStubElementType(SpadLanguage.INSTANCE, stubFactory.getVersion());
    private static final AldorElementTypeFactory instance = new AldorElementTypeFactory();

    private final Map<String, IElementType> factoryForName = Maps.newHashMap();

    AldorElementTypeFactory() {
        factoryForName.put(".*_DEFINE", new AldorDefineElementType(CONSTANT, stubFactory.defineCodec(AldorDefineMixin::new)));
        factoryForName.put(".*_MACRO", new AldorDefineElementType(MACRO, stubFactory.defineCodec(AldorMacroMixin::new)));
        factoryForName.put("COLON_EXPR", new AldorDeclareElementType("SpadDeclare", stubFactory.declareCodec(AldorColonExprFixedImpl::new)) {
            @Override
            public PsiElement createElement(ASTNode node) {
                return new AldorColonExprFixedImpl(node);
            }
        });
        factoryForName.put("DECL_PART", new AldorDeclareElementType("AldorDeclare", stubFactory.declareCodec(AldorDeclPartImpl::new)));
        factoryForName.put("SPAD_ABBREV_CMD", SPAD_ABBREV_ELEMENT_TYPE);
        factoryForName.put("ALDOR_FILE", ALDOR_FILE_ELEMENT_TYPE);
        factoryForName.put("SPAD_FILE", SPAD_FILE_ELEMENT_TYPE);
        factoryForName.put("WHERE_BLOCK", new EmptyStubElementType<AldorWhereBlock>("Where", AldorLanguage.INSTANCE, AldorWhereBlockImpl::new));
        factoryForName.put("UNARY_ADD", new EmptyStubElementType<>("UnaryAdd", AldorLanguage.INSTANCE, AldorUnaryAddImpl::new));

        factoryForName.put("UNARY_WITH", new EmptyStubElementType<AldorWith>("WithPart", AldorLanguage.INSTANCE, AldorUnaryWithImpl::new));
        factoryForName.put("WITH_PART", new EmptyStubElementType<AldorWith>("BinaryWith", AldorLanguage.INSTANCE, AldorWithPartImpl::new));
        factoryForName.put("UNARY_WITH_EXPR", new EmptyStubElementType<AldorWith>("UnaryWithExpr", AldorLanguage.INSTANCE, AldorUnaryWithExprImpl::new));
        factoryForName.put("BINARY_WITH_EXPR", new EmptyStubElementType<AldorWith>("BinaryWithExpr", AldorLanguage.INSTANCE, AldorBinaryWithExprImpl::new));
    }

    public static IElementType createElement(String name) {
        return instance.createElementImpl(name);
    }

    public IElementType createElementImpl(String name) {
        /* Factory should be used once, so can be a bit expensive here */
        for (Map.Entry<String, IElementType> ent: factoryForName.entrySet()) {
            if (name.matches(ent.getKey())) {
                return ent.getValue();
            }
        }

        return new AldorElementType(name);
    }

    public static final class FileStubElementType extends IStubFileElementType<PsiFileStub<PsiFile>> {
        private final int stubVersion;

        private FileStubElementType(Language language, int stubVersion) {
            super("aldorFile", language);
            this.stubVersion = stubVersion;
        }

        @Override
        public int getStubVersion() {
            return stubVersion;
        }
    }

}
