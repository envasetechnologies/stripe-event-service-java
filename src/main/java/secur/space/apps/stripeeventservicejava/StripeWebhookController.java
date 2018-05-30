package secur.space.apps.stripeeventservicejava;

import com.google.gson.Gson;
import com.stripe.Stripe;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;

@RestController
public class StripeWebhookController {

    @Value("${STRIPE_API_KEY")
    String stripeApiKey;

    @Value("${STRIPE_WEBHOOK_SECRET}")
    String stripeWebhookSecret;

    @PostMapping(value = "/api/stripe-events")
    public void postEventsWebhook(HttpServletRequest request, HttpServletResponse response) {

        Stripe.apiKey = stripeApiKey;

        try {
            String payload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

            if (!StringUtils.isEmpty(payload)) {
                String sigHeader = request.getHeader("Stripe-Signature");
                String endpointSecret = stripeWebhookSecret;

                Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

                if ("charge.refunded".equalsIgnoreCase(event.getType())) {

                    Gson gson = new Gson();
                    Charge charge = gson.fromJson(event.getData().getObject().toJson(), Charge.class);
                    // Call your service here with your Charge object.
                }

                response.setStatus(200);
            }

        } catch (Exception e) {
            response.setStatus(500);
        }
    }
}