/*
 *  Copyright 2023 Curity AB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
