package guru.qa.niffler.jupiter;

import guru.qa.niffler.model.UserJson;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UserQueueExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    /*
    biba - wait friendship with boba
    boba - have invite for friendship from biba
    pupa - friend with lupa
    lupa - friens with pupa
     */

    public static ExtensionContext.Namespace NAMESPASE = ExtensionContext.Namespace.create(UserQueueExtension.class);
    public static Map<User.UserType, Queue<UserJson>> usersQueue = new ConcurrentHashMap<>();

    static {
        Queue<UserJson> withoutFriendsUsersQueue = new ConcurrentLinkedQueue<>();
        withoutFriendsUsersQueue.add(UserJson.builder().username("qanva").password("123").build());
        Queue<UserJson> withFriendsUsersQueue = new ConcurrentLinkedQueue<>();
        withFriendsUsersQueue.add(UserJson.builder().username("pupa").password("123").build());
        withFriendsUsersQueue.add(UserJson.builder().username("lupa").password("123").build());
        Queue<UserJson> withInvitesUsersQueue = new ConcurrentLinkedQueue<>();
        withInvitesUsersQueue.add(UserJson.builder().username("boba").password("123").build());
        Queue<UserJson> sentInviteUsersQueue = new ConcurrentLinkedQueue<>();
        sentInviteUsersQueue.add(UserJson.builder().username("biba").password("123").build());
        usersQueue.put(User.UserType.WITH_FRIENDS, withFriendsUsersQueue);
        usersQueue.put(User.UserType.INVITATION_RECEIVED, withInvitesUsersQueue);
        usersQueue.put(User.UserType.INVITATION_SENT, sentInviteUsersQueue);
        usersQueue.put(User.UserType.WITHOUT_FRIENDS, withoutFriendsUsersQueue);

    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Parameter[] parameters = context.getRequiredTestMethod().getParameters();
        for (Parameter parameter : parameters) {
            if (parameter.getType().isAssignableFrom(UserJson.class)) {
                User paramAnnotation = parameter.getAnnotation(User.class);
                User.UserType type = paramAnnotation.userType();
                Queue<UserJson> usersQueueByType = usersQueue.get(type);
                UserJson candidate = null;
                while (candidate == null) {
                    candidate = usersQueueByType.poll();
                }
                context.getStore(NAMESPASE).put(context.getUniqueId(),
                        UserJson.builder()
                                .username(candidate.username())
                                .password(candidate.password())
                                .userType(type).build());
                break;
            }
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        UserJson usedUser = context.getStore(NAMESPASE).get(context.getUniqueId(), UserJson.class);
        usersQueue.get(usedUser.userType()).add(usedUser);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPASE).get(extensionContext.getUniqueId(),UserJson.class);
    }

    private String getAllureId(ExtensionContext extensionContext){
        AllureId allureId = extensionContext.getRequiredTestMethod().getAnnotation(AllureId.class);
        if (allureId == null){
            throw new IllegalStateException("Annotation AllureId is not present");
        }
        return allureId.value();
    }

}
