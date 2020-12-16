### DiscordWhitelist
A 1.8-1.16+ Discord <-> Minecraft whitelisting plugin.

### Placeholders

> `%discord-whitelist_whitelist-status%`<nl>
> Returns True/False depending if the parsed user is whitelisted<nl>
<br>
> ie. `True`<nl>

> `%discord-whitelist_whitelist-time%`<nl>
> Returns the Date time when the user whitelisted<nl>
<br>
> ie. `Wed Dec 16 18:27:38 CET 2020`

> `%discord-whitelist_discord-id%`<nl>
> Returns the Whitelisted Users Discord Identifier if present<nl>
<br>
> ie. `307160296714403851`

> `%discord-whitelist_discord-discrim%`<nl>
> Returns the Whitelist Users Discord Discriminator<nl>
<br>
> ie. `Frosty#3308`

> `%discord-whitelist_discord-name%`<nl>
> Returns the Whitelist Users Discord Name<nl>
<br>
> ie. `Frosty`

### Commands

# Bukkit

> `/whitelist` (Permission: `none`)<nl>
> When ran prompts the user with a Clickable text message, which UnWhitelists the user uppon interaction.

# Discord
> `-whitelist`<nl>
>  - Arguments:

>   `-whitelist <code>`<nl>
>   When ran whitelists the sending user if the code is valid, and the user is not already whitelisted
