<#-- @ftlvariable name="serverOn" type="Boolean" -->
<!DOCTYPE html>
<html lang="en">
<head>
    <#include 'headers.ftl'>
    <title>Minecraft Server - Controls</title>
    <link rel="stylesheet" href="/files/css/minecraftStyle.css"/>
</head>
<body style="text-align: center; font-family: sans-serif">
    <nav>
        <ul>
            <li><b>Minecraft Server config</b></li>
            <li><i>by koeltv</i></li>
            <li style="float: right"><a href="/logout">Disconnect</a></li>
            <li style="float: right"><a target="_blank"
                                        href="https://files.minecraftforge.net/net/minecraftforge/forge/">Get Forge
                    Server Installer</a></li>
        </ul>
    </nav>
    <br>
    <div>
        <ul id="events">
        </ul>
        <script src="/files/sse.js"></script>
    </div>

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