package io.curity.identityserver.plugins.AccountChooser

import se.curity.identityserver.sdk.plugin.descriptor.AuthenticationActionPluginDescriptor

class AccountChooserAuthenticationActionDescriptor: AuthenticationActionPluginDescriptor<AccountChooserAuthenticationActionConfig>
{
    override fun getAuthenticationAction() = AccountChooserAuthenticationAction::class.java

    override fun getPluginImplementationType() = "accountchooser"

    override fun getConfigurationType() = AccountChooserAuthenticationActionConfig::class.java;
}
