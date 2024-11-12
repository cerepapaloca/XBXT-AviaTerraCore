# Rangos
para que se vea el rangos tienes que copiar el carater que lo resepresenta, los carateres que selecionado no son utlizados por eso no tiene icono
- sephirah `🟬`
- developer `🟭`
- seraphine `🟮`
- virtue `🟯`
- arbitro `🟱`
- guardian `🟲`
- mod `🟳`
- builder `🟴`
- passcode `🟵`
- default `🟶`
- partner `🟷`
- streamer `🟸`
- youtuber `🟹`
- tiktoker `🟺`
> [!NOTE]
> Obviamente tiene que descargar el resource pack [aqui](https://github.com/cerepapaloca/Avia-Terra/blob/master/assets.zip) poner el la carpeta `resourcepacks` y ya no hace falta descomprimir
----

> [!IMPORTANT]
> Todo lo que implique las base datos hágalo en un hilo aparte porque si no él hace BOON!!
> Lo que tiene que poner en el código
> ```java
> AviaTerraCore.getInstance().enqueueTaskDataBase(() -> {
>   //toda la logica que tenga una base de datos            
> });
> ````
> y para volver al hilo principal
> ```java
> Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () -> {
>    //toda la logica que interactúe con el sevidor
> });
> ````