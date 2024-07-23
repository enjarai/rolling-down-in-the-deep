package dev.enjarai.rollingdowninthedeep;

import nl.enjarai.cicada.api.conversation.ConversationManager;
import nl.enjarai.cicada.api.util.CicadaEntrypoint;
import nl.enjarai.cicada.api.util.JsonSource;

public class CicadaInitializer implements CicadaEntrypoint {
    @Override
    public void registerConversations(ConversationManager conversationManager) {
        conversationManager.registerSource(
            JsonSource.fromUrl("https://raw.githubusercontent.com/enjarai/rolling-down-in-the-deep/master/src/main/resources/cicada/rolling_down_in_the_deep/conversations.json")
                .or(JsonSource.fromResource("cicada/rolling_down_in_the_deep/conversations.json")),
            RollingDownInTheDeep.LOGGER::info
        );
    }
}
