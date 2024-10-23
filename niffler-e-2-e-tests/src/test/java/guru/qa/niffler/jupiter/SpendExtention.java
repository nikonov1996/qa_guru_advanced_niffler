package guru.qa.niffler.jupiter;

import guru.qa.niffler.api.SpendService;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.extension.*;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.Date;

public class SpendExtention implements BeforeEachCallback, ParameterResolver {

    public static ExtensionContext.Namespace NAMESPASE = ExtensionContext.Namespace.create(SpendExtention.class);

    private static final OkHttpClient httpClient = new OkHttpClient().newBuilder().build();
    private static final Retrofit retrofit = new Retrofit.Builder()
            .client(httpClient)
            .baseUrl("http://127.0.0.1:8093")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final SpendService spendService = retrofit.create(SpendService.class);
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Spend spend = context.getRequiredTestMethod().getAnnotation(Spend.class);
        if (spend != null){
            SpendJson spendJson = SpendJson.builder()
                    .username(spend.username())
                    .amount(spend.amount())
                    .currency(spend.currency())
                    .spendDate(new Date())
                    .description(spend.description())
                    .category(
                            CategoryJson.builder()
                                    .name(spend.category())
                                    .username(spend.username())
                                    .build())
                    .build();

            SpendJson createdSpend = spendService.addSpend(spendJson).execute().body();
            context.getStore(NAMESPASE).put("spend",createdSpend);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
    }

    @Override
    public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPASE).get("spend", SpendJson.class);
    }
}
