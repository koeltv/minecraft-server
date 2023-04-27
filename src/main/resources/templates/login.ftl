<!DOCTYPE html>
<html lang="en">
<head>
    <#include 'headers.ftl'>
    <title>Minecraft Server - Login</title>
    <link rel="stylesheet" href="/files/css/loginStyles.css"/>
</head>
<body>
    <form method="post">
        <img src="/files/images/banner.png" alt="logo">
        <div class="title">Admin Connexion</div>
        <div class="input">
            <label>
                <label class="input-label" for="username">Login</label>
                <input type="text" id="username" required name="username"/>
            </label>
        </div>
        <div class="input">
            <label>
                <label class="input-label" for="password">Password</label>
                <input type="password" autocomplete="current-password" required name="password" id="password"/>
            </label>
        </div>
        <div class="buttonWrapper">
            <div class="topButtonDiv">
                <div class="bottomButtonDiv">
                    <button type="submit" class="button" value="Login">Login</button>
                </div>
            </div>
        </div>
    </form>
</body>
</html>
