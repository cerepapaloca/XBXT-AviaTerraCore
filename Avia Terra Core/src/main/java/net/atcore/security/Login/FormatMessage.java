package net.atcore.security.Login;

public enum FormatMessage {
    LINK("""
            <div style="background-color:#2f3136;border-radius:5px;color:#ffffff;font-family:Arial, sans-serif;max-width:500px;padding:20px;">
                <h2 style="color:#ffffff;">Hola %s!!</h2>
                <p>Tiene que ejecutar este comando para iniciar vincular su cuenta</p>
                <p><code style="background-color:#4f545c;border-radius:3px;display:block;margin-bottom:1px;padding:5px;">/link gmail %s</code></p>
                <p style="color:#b9bbbe;font-size:12px;">si no tenias que recibir este mensaje por favor a caso omiso y bórrelo</p>
            </div>
            """, """
            
            ## Hola %s!!
            Tiene que ejecutar este comando para iniciar vincular su cuenta
            ||```
            /link discord %s
            ```||
            > *si no tenias que recibir este mensaje por favor a caso omiso y bórrelo"""
            ),
    GENERIC("""
            <div style="background-color:#2f3136;border-radius:5px;color:#ffffff;font-family:Arial, sans-serif;max-width:500px;padding:20px;">
                <h2 style="color:#ffffff;">Hola %s!!</h2>
                <p>Aquí esta tu código de un solo uso. Este código no se tiene que compartir</p>
                <p><code style="background-color:#4f545c;border-radius:3px;display:block;margin-bottom:1px;padding:5px;">%s</code></p>
                <p style="color:#b9bbbe;font-size:12px;">si no tenias que recibir este mensaje por favor a caso omiso y bórrelo</p>
            </div>
            """, """
            
            ## Hola %s!!
            Aquí esta tu código de un solo uso. Este código no se tiene que compartir
            ||```
            %s
            ```||
            > *si no tenias que recibir este mensaje por favor a caso omiso y bórrelo*""");

    public final String gmail;
    public final String discord;

    FormatMessage(String gmail, String discord) {
        this.gmail = gmail;
        this.discord = discord;
    }
}
