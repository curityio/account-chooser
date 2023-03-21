package io.curity.identityserver.plugins.accountChooser

import se.curity.identityserver.sdk.authenticationaction.completions.ActionCompletionRequestHandler
import se.curity.identityserver.sdk.plugin.descriptor.AuthenticationActionPluginDescriptor

class AccountChooserAuthenticationActionDescriptor: AuthenticationActionPluginDescriptor<AccountChooserAuthenticationActionConfig>
{
    override fun getAuthenticationAction() = AccountChooserAuthenticationAction::class.java

    override fun getPluginImplementationType() = "accountchooser"

    override fun getConfigurationType() = AccountChooserAuthenticationActionConfig::class.java

    override fun getAuthenticationActionRequestHandlerTypes(): MutableMap<String, Class<out ActionCompletionRequestHandler<*>>>
    {
        return mutableMapOf(
            "index" to AccountChooserRequestHandler::class.java
        )
    }

    companion object {
        const val ACTIVE_SESSIONS_ACRS = "active-sessions-acrs"
        const val CHOSEN_ACR = "chosen-acr"
        const val TRANSACTION_ID = "transaction-id"
        const val FORCE_AUTHENTICATION = "force-authentication"
        const val HAS_ACTIVE_SESSIONS = "has-active-sessions"
        const val AUTHENTICATION_ACR = "authentication-acr"
    }
}
