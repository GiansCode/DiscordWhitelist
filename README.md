### DiscordWhitelist
A 1.8-1.16+ Discord <-> Minecraft whitelisting plugin.

### Placeholders

> `%discord-whitelist_whitelist-status%`<br>
> Returns True/False depending if the parsed user is whitelisted

> ie. `True`<nl>

> `%discord-whitelist_whitelist-time%`<br>
> Returns the Date time when the user whitelisted

> ie. `Wed Dec 16 18:27:38 CET 2020`

> `%discord-whitelist_discord-id%`<br>
> Returns the Whitelisted Users Discord Identifier if present

> ie. `307160296714403851`

> `%discord-whitelist_discord-discrim%`<br>
> Returns the Whitelist Users Discord Discriminator

> ie. `Frosty#3308`

> `%discord-whitelist_discord-name%`<br>
> Returns the Whitelist Users Discord Name

> ie. `Frosty`

### Commands

# Bukkit

> `/whitelist` (Permission: `none`)<br>
> When ran prompts the user with a Clickable text message, which UnWhitelists the user uppon interaction.

# Discord
> `-whitelist`<br>
>  - Arguments:

>   `-whitelist <code>`<br>
>   When ran whitelists the sending user if the code is valid, and the user is not already whitelisted
