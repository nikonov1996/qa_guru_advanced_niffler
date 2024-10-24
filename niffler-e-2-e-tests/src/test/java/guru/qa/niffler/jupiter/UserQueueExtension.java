package guru.qa.niffler.jupiter;

import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.extension.*;

public class UserQueueExtension implements BeforeTestExecutionCallback, ParameterResolver {

    ExtensionContext.Namespace NAMESPASE = ExtensionContext.Namespace.create(UserQueueExtension.class);

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
//        context.getStore(NAMESPASE).put("user",
//                UserJson.builder().username("qanva").password("123").build());
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return UserJson.builder().username("qanva").password("123").build();
    }
}
