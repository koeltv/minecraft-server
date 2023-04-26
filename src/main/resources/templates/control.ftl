<#-- @ftlvariable name="serverOn" type="Boolean" -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width"/>
    <link rel="icon" href="/files/images/favicon.ico"/>
    <title>Minecraft Server - Controls</title>
    <link rel="stylesheet" href="/files/css/minecraftStyle.css"/>
</head>
<body style="text-align: center; font-family: sans-serif">
    <nav>
        <ul>
            <li><b>Minecraft Server config</b></li>
            <li><i>by koeltv</i></li>
            <li style="float: right"><a href="/logout">Disconnect</a></li>
        </ul>
    </nav>
    <br>
    <div>
        <form action="/minecraft/start" method="post" class="inlined">
            <input type="submit" value="Start" class="animated-button" <#if serverOn>disabled</#if>>
        </form>
        <form action="/minecraft/stop" method="post" class="inlined">
            <input type="submit" value="Stop" class="animated-button" <#if !serverOn>disabled</#if>>
        </form>
    </div>
</body>
</html>