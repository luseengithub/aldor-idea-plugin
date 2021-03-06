package aldor.build.builders;

import aldor.build.module.AldorModuleType;
import aldor.builder.AldorTargetIds;
import aldor.file.AldorFileType;
import com.google.common.collect.Lists;
import com.intellij.compiler.impl.BuildTargetScopeProvider;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerFilter;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.api.CmdlineRemoteProto.Message.ControllerMessage.ParametersMessage.TargetTypeBuildScope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static aldor.builder.AldorBuildConstants.ALDOR_FILE_TARGET;

/**
 * Maps a change list into a bunch of stuff to do for the compiler
 */
public class AldorBuildTargetScopeProvider extends BuildTargetScopeProvider {
    private static final Logger LOG = Logger.getInstance(AldorBuildTargetScopeProvider.class);

    @NotNull
    @Override
    public List<TargetTypeBuildScope> getBuildTargetScopes(
            @NotNull final CompileScope baseScope,
            @NotNull @SuppressWarnings("deprecation") final CompilerFilter filter,
            @NotNull final Project project,
            final boolean forceBuild) {

        LOG.info("get build targets: " + Arrays.asList(baseScope.getAffectedModules()));

        // Gather the target IDs (module names) of the target modules.
        final Collection<String> targetIds = new ArrayList<>();
        for (final Module module : baseScope.getAffectedModules()) {
            if (ModuleType.get(module).equals(AldorModuleType.instance())) {
                targetIds.add(module.getName());
            }
        }

        VirtualFile[] files = baseScope.getFiles(AldorFileType.INSTANCE, false);

        for (VirtualFile file: files) {
            targetIds.add(AldorTargetIds.aldorFileTargetId(file.getPath()));
        }
        TargetTypeBuildScope req = TargetTypeBuildScope
                .newBuilder()
                .setTypeId(ALDOR_FILE_TARGET)
                .setForceBuild(forceBuild)
                .addAllTargetId(targetIds).build();

        return Lists.newArrayList(req);
    }

}
