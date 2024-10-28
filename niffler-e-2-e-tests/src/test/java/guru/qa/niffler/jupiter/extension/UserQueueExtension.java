package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Parameter;
import java.util.*;
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

        var beforEach = Arrays.stream(context.getRequiredTestClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(BeforeEach.class)).toList();

        List<Parameter> parameters = new ArrayList<>();
        parameters.addAll(Arrays.stream(context.getRequiredTestMethod().getParameters()).toList());
        parameters.addAll(Arrays.stream(beforEach.getFirst().getParameters()).toList());

        Map<User.UserType, UserJson> userJsonMap = new ConcurrentHashMap<>();
        for (Parameter parameter : parameters) {
            User user = parameter.getAnnotation(User.class);
            if (user != null) {
                UserJson candidate = null;
                while (candidate == null) {
                    candidate = usersQueue.get(user.userType()).poll();
                }
                userJsonMap.put(user.userType(), UserJson.builder()
                        .username(candidate.username())
                        .password(candidate.password())
                        .userType(user.userType())
                        .build());
            }
        }
        context.getStore(NAMESPASE).put(context.getUniqueId(), userJsonMap);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        Map<User.UserType, UserJson> usedUsers = (Map<User.UserType, UserJson>) context.getStore(NAMESPASE).get(context.getUniqueId());
        usedUsers.keySet().forEach(userType -> usersQueue.get(userType).add(usedUsers.get(userType)));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class) &&
                parameterContext.getParameter().isAnnotationPresent(User.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        User.UserType currentUserJsonType = parameterContext.getParameter().getAnnotation(User.class).userType();
        Map<User.UserType, UserJson> usedUsers = (Map<User.UserType, UserJson>) extensionContext.getStore(NAMESPASE).get(extensionContext.getUniqueId());
        return usedUsers.get(currentUserJsonType);
    }

}
