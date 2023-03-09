package io.curity.identityserver.plugins.AccountChooser

import se.curity.identityserver.sdk.config.Configuration
import se.curity.identityserver.sdk.config.annotation.Description

interface AccountChooserAuthenticationActionConfig: Configuration
{
    @Description("A parameter used for demo purposes")
    fun getDemoParameter(): Int
}
